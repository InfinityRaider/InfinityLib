package com.infinityraider.infinitylib.block.tile;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.network.MessageAutoSyncTileField;
import com.infinityraider.infinitylib.network.MessageRenderUpdate;
import com.infinityraider.infinitylib.network.MessageSyncTile;
import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

@SuppressWarnings("unused")
public abstract class TileEntityBase extends TileEntity {
    private static final Random RANDOM = new Random();

    private final Map<Integer, AutoSyncedField<?>> syncedFields;

    public TileEntityBase(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        this.syncedFields = Maps.newHashMap();
    }

    public final int xCoord() {
        return this.getPos().getX();
    }

    public final int yCoord() {
        return this.getPos().getY();
    }

    public final int zCoord() {
        return this.getPos().getZ();
    }

    public Random getRandom() {
        return this.getWorld() == null ? RANDOM : this.getWorld().rand;
    }

    public boolean isRemote() {
        return this.getWorld() != null && this.getWorld().isRemote;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), -1, this.getUpdateTag()); //TODO: figure out the int argument
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = new CompoundNBT();
        this.write(tag);
        return tag;
    }

    //read data from packet
    @Override
    public void onDataPacket(NetworkManager networkManager, SUpdateTileEntityPacket pkt){
        if(this.getWorld() == null) {
            return;
        }
        BlockState before = this.getBlockState();
        this.read(before, pkt.getNbtCompound());
        BlockState after = this.getWorld().getBlockState(pkt.getPos());
        if(!after.equals(before)) {
            this.getWorld().markBlockRangeForRenderUpdate(pkt.getPos(), before, after);
        }
    }

    @Nonnull
    @Override
    public final CompoundNBT write(@Nonnull CompoundNBT tag) {
        super.write(tag);
        // Order shouldn't matter here
        this.syncedFields.values().forEach(field -> tag.put(Names.NBT.FIELD + "_" + field.getId(), field.serialize()));
        this.writeTileNBT(tag);
        return tag;
    }

    @Override
    public final void read(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        super.read(state, tag);
        // Again, order doesn't matter
        this.syncedFields.values().forEach(field -> {
            String key = Names.NBT.FIELD + "_" + field.getId();
            if(tag.contains(key)) {
                field.deserialize(tag.getCompound(key));
            }
        });
        this.readTileNBT(state, tag);
    }

    protected abstract void writeTileNBT(@Nonnull CompoundNBT tag);

    protected abstract void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag);

    public void markForUpdateAndNotify() {
        if(this.getWorld() != null) {
            BlockState state = this.getBlockState();
            this.getWorld().notifyBlockUpdate(getPos(), state, state, 3);
            this.markDirty();
        }
    }

    public void syncToClient() {
        this.syncToClient(false);
    }

    public void syncToClient(boolean renderUpdate) {
        World world = this.getWorld();
        if(world != null && !this.getWorld().isRemote) {
            new MessageSyncTile(this, renderUpdate).sendToAllAround(this.getWorld(), this.xCoord(), this.yCoord(), this.zCoord(), 128);
        }
    }

    public void forceRenderUpdate() {
        if(this.isRemote()) {
            InfinityLib.instance.proxy().forceClientRenderUpdate(this.getPos());
        } else {
            if(this.getWorld() != null) {
                new MessageRenderUpdate(this.getPos()).sendToDimension(this.getWorld());
            }
        }
    }

    /**
     * Method to create fields which are automatically synced between server and the client, as well as saved to disk
     * Only call set method on the server
     *
     * @param value The initial value of the field (will not be synced initially, must match server and client)
     * @param serializer The serializer used to write the value to NBT
     * @param deserializer The deserializer used to read the value from NBT
     * @param <F> The type of the field
     * @return a new AutoSyncedField object, wrapping the desired value
     */
    protected <F> AutoSyncedField<F> createField(
            F value, BiConsumer<F, CompoundNBT> serializer, Function<CompoundNBT, F> deserializer) {

        AutoSyncedField<F> field = new AutoSyncedField<>(value, this.syncedFields.size(), this, serializer, deserializer);
        this.syncedFields.put(field.getId(), field);
        return field;
    }

    /**
     * Method to create fields which are automatically synced between server and the client, as well as saved to disk
     * Only call set method on the server
     *
     * This method differs with the above one in that it allows for fields to be read from disk with a delay,
     * For instance if other tasks need to finish first during serverStarting or serverAboutToStart.
     *
     * @param value The initial value of the field (will not be synced initially, must match server and client)
     * @param serializer The serializer used to write the value to NBT
     * @param deserializer The deserializer used to read the value from NBT
     * @param checker Checks if the data is ready to be read from disk
     * @param fallback The value to be returned while data has not yet been read from disk
     * @param <F> The type of the field
     * @return a new AutoSyncedField object, wrapping the desired value
     */
    protected <F> AutoSyncedField<F> createField(
            F value, BiConsumer<F, CompoundNBT> serializer, Function<CompoundNBT, F> deserializer, BooleanSupplier checker, F fallback) {

        AutoSyncedField<F> field = new AutoSyncedFieldDelayed<>(value, this.syncedFields.size(), this, serializer, deserializer, checker, fallback);
        this.syncedFields.put(field.getId(), field);
        return field;
    }

    @SuppressWarnings("unchecked")
    public <F> AutoSyncedField<F> getField(int id) {
        // Cast should not be an issue here
        return (AutoSyncedField<F>) this.syncedFields.get(id);
    }

    public static class AutoSyncedField<F> {
        private F value;

        private final int id;
        private final TileEntityBase tile;
        private final LogicalSide side;

        private final BiConsumer<F, CompoundNBT> serializer;
        private final Function<CompoundNBT, F> deserializer;

        private AutoSyncedField(F value, final int id, TileEntityBase tile, BiConsumer<F, CompoundNBT> serializer, Function<CompoundNBT, F> deserializer) {
            this.value = value;
            this.id = id;
            this.tile = tile;
            this.side = InfinityLib.instance.proxy().getLogicalSide();
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        public void set(F value) {
            if(this.getSide().isServer() && !this.get().equals(value)) {
                this.setInternal(value);
                this.sync();
                this.getTile().markDirty();
            }
        }

        // Do not call this directly, called by the message handler to set the field on the client
        public void setClient(F value) {
            if(this.getSide().isClient()) {
                this.setInternal(value);
            }
        }

        protected void setInternal(F value) {
            this.value = value;
        }

        public F get() {
            return this.value;
        }

        public int getId() {
            return this.id;
        }

        public TileEntityBase getTile() {
            return this.tile;
        }

        public LogicalSide getSide() {
            return this.side;
        }

        public CompoundNBT serialize() {
            CompoundNBT tag = new CompoundNBT();
            this.serializer.accept(this.get(), tag);
            return tag;
        }

        public void deserialize(CompoundNBT tag) {
            this.setInternal(this.deserializer.apply(tag));
        }

        protected void sync() {
            new MessageAutoSyncTileField<>(this).sendToAll();
        }
    }

    protected static class AutoSyncedFieldDelayed<F> extends AutoSyncedField<F> {
        private final BooleanSupplier checker;
        private final F fallback;

        private CompoundNBT data;

        private AutoSyncedFieldDelayed(F value, int id, TileEntityBase tile, BiConsumer<F, CompoundNBT> serializer, Function<CompoundNBT, F> deserializer,
                                       BooleanSupplier checker, F fallback) {
            super(value, id, tile, serializer, deserializer);
            this.checker = checker;
            this.fallback = fallback;
        }

        @Override
        protected void setInternal(F value) {
            this.data = null;
            super.setInternal(value);
        }

        @Override
        public F get() {
            if(this.data == null) {
                return super.get();
            } else {
                if(this.isReady()) {
                    this.deserialize(this.data);
                    this.data = null;
                    if(this.getSide().isServer()) {
                        this.sync();
                    }
                    return this.get();
                } else {
                    return this.fallback;
                }
            }
        }

        @Override
        public void deserialize(CompoundNBT tag) {
            if(this.isReady()) {
                super.deserialize(tag);
            } else {
                this.data = tag.copy();
            }
        }

        @Override
        public CompoundNBT serialize() {
            if(this.data == null) {
                return super.serialize();
            }
            if(this.isReady()) {
                super.deserialize(this.data);
                this.data = null;
                return super.serialize();
            }
            return this.data;
        }

        protected boolean isReady() {
            return this.checker.getAsBoolean();
        }
    }
}
