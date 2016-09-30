package com.infinityraider.infinitylib.block.tile;

import com.infinityraider.infinitylib.network.MessageSyncTile;
import com.infinityraider.infinitylib.network.NetworkWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public abstract class TileEntityBase extends TileEntity {

    public final int xCoord() {
        return this.getPos().getX();
    }

    public final int yCoord() {
        return this.getPos().getY();
    }

    public final int zCoord() {
        return this.getPos().getZ();
    }

    public IBlockState getActualState(IBlockState state) {
        return state;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return tag;
    }

    //read data from packet
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }

    @Override
    public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.writeTileNBT(tag);
        return tag;
    }

    @Override
    public final void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.readTileNBT(tag);
    }

    protected abstract void writeTileNBT(NBTTagCompound tag);

    protected abstract void readTileNBT(NBTTagCompound tag);

    public void markForUpdate() {
        IBlockState state = worldObj.getBlockState(this.getPos());
        worldObj.notifyBlockUpdate(getPos(), state, state, 3);
        this.markDirty();
    }

    public void syncToClient() {
        this.syncToClient(false);
    }

    public void syncToClient(boolean renderUpdate) {
        if(!this.worldObj.isRemote) {
            NetworkWrapper.getInstance().sendToAllAround(new MessageSyncTile(this, renderUpdate), this.worldObj, this.xCoord(), this.yCoord(), this.zCoord(), 128);
        }
    }
}
