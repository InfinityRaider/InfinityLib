package com.infinityraider.infinitylib.network;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSyncTile extends MessageBase<IMessage> {
    private String className;
    private int dimension;
    private BlockPos pos;
    private NBTTagCompound tag;
    private boolean renderUpdate;

    public MessageSyncTile() {
        super();
    }

    public MessageSyncTile(TileEntity tile, boolean renderUpdate) {
        this();
        this.className = tile.getClass().getName();
        this.dimension = tile.getWorld().provider.getDimension();
        this.pos = tile.getPos();
        this.tag = tile.writeToNBT(new NBTTagCompound());
        this.renderUpdate = renderUpdate;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        World world = InfinityLib.proxy.getClientWorld();
        if (world.provider.getDimension() == this.dimension) {
            TileEntity te = world.getTileEntity(this.pos);
            if (te != null && te.getClass().toString().equals(this.className)) {
                te.readFromNBT(this.tag);
                if (this.renderUpdate) {
                    world.markBlockRangeForRenderUpdate(this.pos, this.pos);
                }
            }
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
