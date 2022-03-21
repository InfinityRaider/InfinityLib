package com.infinityraider.infinitylib.network;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageSyncTile extends MessageBase {
    private String className;
    private BlockEntity tile;
    private CompoundTag tag;
    private BlockPos pos;
    private boolean renderUpdate;

    public MessageSyncTile() {
        super();
    }

    public MessageSyncTile(BlockEntity tile, boolean renderUpdate) {
        this();
        this.className = tile.getClass().getName();
        this.tile = tile;
        this.pos = tile.getBlockPos();
        this.tag = tile.saveWithFullMetadata();
        this.renderUpdate = renderUpdate;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        Level world = InfinityLib.instance.getClientWorld();
        if (this.tile != null && this.tile.getClass().toString().equals(this.className)) {
            BlockState pre = world.getBlockState(this.pos);
            this.tile.load(this.tag);
            if (this.renderUpdate) {
                world.setBlocksDirty(this.tile.getBlockPos(), pre, world.getBlockState(this.pos));
            }
        }
    }
}
