package com.infinityraider.infinitylib.network;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSyncTile extends MessageBase {
    private String className;
    private TileEntity tile;
    private CompoundNBT tag;
    private BlockPos pos;
    private boolean renderUpdate;

    public MessageSyncTile() {
        super();
    }

    public MessageSyncTile(TileEntity tile, boolean renderUpdate) {
        this();
        this.className = tile.getClass().getName();
        this.tile = tile;
        this.pos = tile.getPos();
        this.tag = tile.write(new CompoundNBT());
        this.renderUpdate = renderUpdate;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        World world = InfinityLib.instance.getClientWorld();
        if (this.tile != null && this.tile.getClass().toString().equals(this.className)) {
            BlockState pre = world.getBlockState(this.pos);
            this.tile.read(pre, this.tag);
            if (this.renderUpdate) {
                world.markBlockRangeForRenderUpdate(this.tile.getPos(), pre, world.getBlockState(this.pos));
            }
        }
    }
}
