package com.infinityraider.infinitylib.block.tile;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.network.MessageAutoSyncTileField;
import com.infinityraider.infinitylib.network.MessageRenderUpdate;
import com.infinityraider.infinitylib.network.MessageSyncTile;
import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Random;
import java.util.function.*;

@SuppressWarnings("unused")
public abstract class TileEntityBase extends BlockEntity {
    private static final Random RANDOM = new Random();

    private final Map<Integer, AutoSyncedField<?>> syncedFields;

    public TileEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.syncedFields = Maps.newHashMap();
    }

    public final int xCoord() {
        return this.getBlockPos().getX();
    }

    public final int yCoord() {
        return this.getBlockPos().getY();
    }

    public final int zCoord() {
        return this.getBlockPos().getZ();
    }

    public final ChunkAccess getChunk() {
        if(this.getLevel() == null) {
            return null;
        }
        return this.getLevel().getChunk(this.getBlockPos());
    }

    public Random getRandom() {
        return this.getLevel() == null ? RANDOM : this.getLevel().getRandom();
    }

    public boolean isRemote() {
        return this.getLevel() != null && this.getLevel().isClientSide();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithFullMetadata();
    }

    //read data from packet
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
        if(this.getLevel() == null) {
            return;
        }
        CompoundTag tag = pkt.getTag();
        if(tag != null) {
            BlockState before = this.getBlockState();
            this.load(tag);
            BlockState after = this.getLevel().getBlockState(pkt.getPos());
            if (!after.equals(before)) {
                this.getLevel().setBlocksDirty(pkt.getPos(), before, after);
            }
        }
    }

    @Nonnull
    @Override
    public final void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        // Order shouldn't matter here
        this.syncedFields.values().forEach(field -> tag.put(Names.NBT.FIELD + "_" + field.getId(), field.serialize()));
        this.writeTileNBT(tag);
    }

    @Override
    public final void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        // Again, order doesn't matter
        this.syncedFields.values().forEach(field -> {
            String key = Names.NBT.FIELD + "_" + field.getId();
            if(tag.contains(key)) {
                field.deserialize(tag.getCompound(key));
            }
        });
        this.readTileNBT(tag);
    }

    protected abstract void writeTileNBT(@Nonnull CompoundTag tag);

    protected abstract void readTileNBT(@Nonnull CompoundTag tag);

    public void markForUpdateAndNotify() {
        if(this.getLevel() != null) {
            BlockState state = this.getBlockState();
            this.getLevel().sendBlockUpdated(getBlockPos(), state, state, 3);
            this.setChanged();
        }
    }

    public void syncToClient() {
        this.syncToClient(false);
    }

    public void syncToClient(boolean renderUpdate) {
        Level world = this.getLevel();
        if(world != null && !this.getLevel().isClientSide()) {
            new MessageSyncTile(this, renderUpdate).sendToAllAround(this.getLevel(), this.xCoord(), this.yCoord(), this.zCoord(), 128);
        }
    }

    public void forceRenderUpdate() {
        if(this.isRemote()) {
            InfinityLib.instance.proxy().forceClientRenderUpdate(this.getBlockPos());
        } else {
            if(this.getLevel() != null) {
                new MessageRenderUpdate(this.getBlockPos()).sendToDimension(this.getLevel());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <F> AutoSyncedField<F> getField(int id) {
        // Cast should not be an issue here
        return (AutoSyncedField<F>) this.syncedFields.get(id);
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
    protected <F> AutoSyncedField<F> createAutoSyncedField(
            F value, BiConsumer<F, CompoundTag> serializer, Function<CompoundTag, F> deserializer) {
        return this.getAutoSyncedFieldBuilder(value, serializer, deserializer).build();
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
    protected <F> AutoSyncedField<F> createAutoSyncedField(
            F value, BiConsumer<F, CompoundTag> serializer, Function<CompoundTag, F> deserializer, BooleanSupplier checker, F fallback) {
        return this.getAutoSyncedFieldBuilder(value, serializer, deserializer).withDelay(checker, fallback).build();
    }

    public <F> AutoSyncedFieldBuilder<F> getAutoSyncedFieldBuilder(
            F value, BiConsumer<F, CompoundTag> serializer, Function<CompoundTag, F> deserializer, BooleanSupplier checker, F fallback) {
        return new AutoSyncedFieldBuilder<>(value, this, serializer, deserializer).withDelay(checker, fallback);
    }

    public <F> AutoSyncedFieldBuilder<F> getAutoSyncedFieldBuilder(
            F value, BiConsumer<F, CompoundTag> serializer, Function<CompoundTag, F> deserializer) {
        return new AutoSyncedFieldBuilder<>(value, this, serializer, deserializer);
    }

    public AutoSyncedFieldBuilder<Boolean> getAutoSyncedFieldBuilder(boolean value) {
        return new AutoSyncedFieldBuilder<Boolean>(
                value, this,
                (b, tag) -> tag.putBoolean(Names.NBT.VALUE, b),
                (tag) -> tag.contains(Names.NBT.VALUE) ? tag.getBoolean(Names.NBT.VALUE) : value
        );
    }

    public AutoSyncedFieldBuilder<Integer> getAutoSyncedFieldBuilder(int value) {
        return new AutoSyncedFieldBuilder<>(
                value, this,
                (i, tag) -> tag.putInt(Names.NBT.VALUE, i),
                (tag) -> tag.contains(Names.NBT.VALUE) ? tag.getInt(Names.NBT.VALUE) : value
        );
    }

    public AutoSyncedFieldBuilder<Float> getAutoSyncedFieldBuilder(float value) {
        return new AutoSyncedFieldBuilder<>(
                value, this,
                (f, tag) -> tag.putFloat(Names.NBT.VALUE, f),
                (tag) -> tag.contains(Names.NBT.VALUE) ? tag.getFloat(Names.NBT.VALUE) : value
        );
    }

    public AutoSyncedFieldBuilder<Double> getAutoSyncedFieldBuilder(double value) {
        return new AutoSyncedFieldBuilder<>(
                value, this,
                (d, tag) -> tag.putDouble(Names.NBT.VALUE, d),
                (tag) -> tag.contains(Names.NBT.VALUE) ? tag.getDouble(Names.NBT.VALUE) : value
        );
    }

    public AutoSyncedFieldBuilder<String> getAutoSyncedFieldBuilder(String value) {
        return new AutoSyncedFieldBuilder<>(
                value, this,
                (s, tag) -> tag.putString(Names.NBT.VALUE, s),
                (tag) -> tag.contains(Names.NBT.VALUE) ? tag.getString(Names.NBT.VALUE) : value
        );
    }

    public AutoSyncedFieldBuilder<ItemStack> getAutoSyncedFieldBuilder(ItemStack value) {
        return new AutoSyncedFieldBuilder<ItemStack>(
                value, this,
                ItemStack::save,
                ItemStack::of
        );
    }

    public AutoSyncedFieldBuilder<BlockPos> getAutoSyncedFieldBuilder(BlockPos value) {
        return new AutoSyncedFieldBuilder<>(
                value, this,
                (pos, tag) -> {tag.putInt(Names.NBT.X, pos.getX());tag.putInt(Names.NBT.Y, pos.getY());tag.putInt(Names.NBT.Z, pos.getZ());},
                (tag) -> {
                    if(tag.contains(Names.NBT.X) && tag.contains(Names.NBT.Y) && tag.contains(Names.NBT.Z)) {
                        return new BlockPos(tag.getInt(Names.NBT.X), tag.getInt(Names.NBT.Y), tag.getInt(Names.NBT.Z));
                    } else {
                        return this.getBlockPos();
                    }
                }
        );
    }

    public static class AutoSyncedFieldBuilder<F> {
        // Required fields
        private final F value;
        private final TileEntityBase tile;
        private final BiConsumer<F, CompoundTag> serializer;
        private final Function<CompoundTag, F> deserializer;

        // Callback
        private Consumer<F> callback;

        // Render update
        private Predicate<F> renderUpdateChecker;

        // Delay
        private boolean delayed;
        private BooleanSupplier delayedCheck;
        private F fallbackValue;

        private AutoSyncedFieldBuilder(F value, TileEntityBase tile, BiConsumer<F, CompoundTag> serializer, Function<CompoundTag, F> deserializer) {
            // Required fields
            this.value = value;
            this.tile = tile;
            this.serializer = serializer;
            this.deserializer = deserializer;
            // Optional fields
            this.callback = f -> {};
            this.renderUpdateChecker = f -> false;
            this.delayed = false;
        }

        public AutoSyncedFieldBuilder<F> withCallBack(Consumer<F> callback) {
            this.callback = callback;
            return this;
        }

        public AutoSyncedFieldBuilder<F> withRenderUpdate() {
            return this.withRenderUpdate(f -> true);
        }

        public AutoSyncedFieldBuilder<F> withRenderUpdate(Predicate<F> renderUpdateChecker) {
            this.renderUpdateChecker = renderUpdateChecker;
            return this;
        }

        public AutoSyncedFieldBuilder<F> withDelay(BooleanSupplier isReady, F tempValue) {
            this.delayed = true;
            this.delayedCheck = isReady;
            this.fallbackValue = tempValue;
            return this;
        }

        public AutoSyncedField<F> build() {
            if (this.delayed) {
                return new AutoSyncedFieldDelayed<>(
                        this.value, this.tile, this.serializer, this.deserializer, this.callback, this.renderUpdateChecker,
                        this.delayedCheck, this.fallbackValue
                );
            }
            return new AutoSyncedField<>(
                    this.value, this.tile, this.serializer, this.deserializer, this.callback, this.renderUpdateChecker);
        }

    }

    public static class AutoSyncedField<F> {
        private F value;

        private final int id;
        private final TileEntityBase tile;
        private final LogicalSide side;

        private final BiConsumer<F, CompoundTag> serializer;
        private final Function<CompoundTag, F> deserializer;

        private final Consumer<F> callback;
        private final Predicate<F> renderUpdateChecker;

        private AutoSyncedField(
                F value, TileEntityBase tile, BiConsumer<F, CompoundTag> serializer, Function<CompoundTag, F> deserializer,
                Consumer<F> callback, Predicate<F> renderUpdateChecker) {

            this.value = value;
            this.id = tile.syncedFields.size();
            tile.syncedFields.put(this.getId(), this);
            this.tile = tile;
            this.side = InfinityLib.instance.proxy().getLogicalSide();
            this.serializer = serializer;
            this.deserializer = deserializer;
            this.callback = callback;
            this.renderUpdateChecker = renderUpdateChecker;
        }

        public void set(F value) {
            if(this.getSide().isServer() && !this.get().equals(value)) {
                this.setInternal(value);
                this.sync();
                this.getTile().setChanged();
            }
        }

        // Do not call this directly, called by the message handler to set the field on the client
        protected void setInternal(F value) {
            if(this.value == value) {
                return;
            }
            this.value = value;
            this.callback.accept(value);
            if(this.getSide().isClient() && this.renderUpdateChecker.test(value)) {
                this.getTile().forceRenderUpdate();
            }
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

        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            this.serializer.accept(this.get(), tag);
            return tag;
        }

        public void deserialize(CompoundTag tag) {
            this.setInternal(this.deserializer.apply(tag));
        }

        protected void sync() {
            new MessageAutoSyncTileField<>(this).sendToAll();
        }
    }

    protected static class AutoSyncedFieldDelayed<F> extends AutoSyncedField<F> {
        private final BooleanSupplier checker;
        private final F fallback;

        private CompoundTag data;

        private AutoSyncedFieldDelayed(F value, TileEntityBase tile, BiConsumer<F, CompoundTag> serializer, Function<CompoundTag, F> deserializer,
                                       Consumer<F> callback, Predicate<F> renderUpdateChecker, BooleanSupplier checker, F fallback) {
            super(value, tile, serializer, deserializer, callback, renderUpdateChecker);
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
        public void deserialize(CompoundTag tag) {
            if(this.isReady()) {
                super.deserialize(tag);
            } else {
                this.data = tag.copy();
            }
        }

        @Override
        public CompoundTag serialize() {
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
