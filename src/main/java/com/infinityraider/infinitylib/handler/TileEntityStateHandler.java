package com.infinityraider.infinitylib.handler;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.block.tile.ITileEntityStateCache;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class TileEntityStateHandler implements IWorldEventListener {
    private static final TileEntityStateHandler INSTANCE = new TileEntityStateHandler();

    public static TileEntityStateHandler getInstance() {
        return INSTANCE;
    }

    private boolean registered = false;

    private TileEntityStateHandler() {}

    public TileEntityStateHandler register() {
        if(!this.registered) {
            InfinityLib.proxy.registerEventHandler(this);
            this.registered = true;
        }
        return this;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onWorldLoad(WorldEvent.Load event) {
        event.getWorld().addEventListener(this);
    }

    @Override
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        TileEntity te = worldIn.getTileEntity(pos);
        if(te instanceof ITileEntityStateCache) {
            ((ITileEntityStateCache) te).resetSate(newState);
        }
    }

    @Override
    public void notifyLightSet(BlockPos pos) {

    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {

    }

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {

    }

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos) {

    }

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {

    }

    @Override
    public void onEntityAdded(Entity entityIn) {

    }

    @Override
    public void onEntityRemoved(Entity entityIn) {

    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {

    }

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {

    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {

    }
}
