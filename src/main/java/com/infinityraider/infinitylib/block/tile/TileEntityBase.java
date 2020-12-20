package com.infinityraider.infinitylib.block.tile;

import com.infinityraider.infinitylib.network.MessageSyncTile;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;

import java.util.Random;

@SuppressWarnings("unused")
public abstract class TileEntityBase extends TileEntity {
    public TileEntityBase(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public BlockState getState() {
        return this.getWorld().getBlockState(this.getPos());
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
        return this.getWorld().rand;
    }

    public boolean isRemote() {
        return this.getWorld().isRemote;
    }

    public IChunk getChunk() {
        return this.getWorld().getChunk(this.getPos());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), -1, this.getUpdateTag()); //TODO: figure out the int argument
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = new CompoundNBT();
        this.write(tag);
        return tag;
    }

    //read data from packet
    @Override
    public void onDataPacket(NetworkManager networkManager, SUpdateTileEntityPacket pkt){
        BlockState before = this.getWorld().getBlockState(pkt.getPos());
        this.read(before, pkt.getNbtCompound());
        BlockState after = this.getWorld().getBlockState(pkt.getPos());
        if(!after.equals(before)) {
            this.getWorld().markBlockRangeForRenderUpdate(pkt.getPos(), before, after);
        }
    }

    @Override
    public final CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        this.writeTileNBT(tag);
        return tag;
    }

    @Override
    public final void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        this.readTileNBT(state, tag);
    }

    protected abstract void writeTileNBT(CompoundNBT tag);

    protected abstract void readTileNBT(BlockState state, CompoundNBT tag);

    public void markForUpdate() {
        BlockState state = this.getWorld().getBlockState(this.getPos());
        this.getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        this.markDirty();
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
}
