package com.infinityraider.infinitylib.network;

import com.infinityraider.infinitylib.network.serialization.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public abstract class MessageBase<REPLY extends IMessage> implements IMessage {
    public MessageBase() {
        super();
    }

    public abstract Side getMessageHandlerSide();

    protected abstract void processMessage(MessageContext ctx);

    protected abstract REPLY getReply(MessageContext ctx);

    protected String readStringFromByteBuf(ByteBuf buf) {
        return ByteBufUtil.readString(buf);
    }

    protected ByteBuf writeStringToByteBuf(ByteBuf buf, String string) {
        return ByteBufUtil.writeString(buf, string);
    }

    protected EntityPlayer readPlayerFromByteBuf(ByteBuf buf) {
        return ByteBufUtil.readPlayer(buf);
    }

    protected ByteBuf writePlayerToByteBuf(ByteBuf buf, EntityPlayer player) {
        return ByteBufUtil.writePlayer(buf, player);
    }

    protected Entity readEntityFromByteBuf(ByteBuf buf) {
        return ByteBufUtil.readEntity(buf);
    }

    protected ByteBuf writeEntityToByteBuf(ByteBuf buf, Entity e) {
        return ByteBufUtil.writeEntity(buf, e);
    }

    protected BlockPos readBlockPosFromByteBuf(ByteBuf buf) {
        return ByteBufUtil.readBlockPos(buf);
    }

    protected ByteBuf writeBlockPosToByteBuf(ByteBuf buf, BlockPos pos) {
        return ByteBufUtil.writeBlockPos(buf, pos);
    }

    protected Block readBlockFromByteBuf(ByteBuf buf) {
        return ByteBufUtil.readBlock(buf);
    }

    protected ByteBuf writeBlockToByteBuf(Block block, ByteBuf buf) {
        return ByteBufUtil.writeBlock(buf, block);
    }

    protected Item readItemFromByteBuf(ByteBuf buf) {
        return ByteBufUtil.readItem(buf);
    }

    protected ByteBuf writeItemToByteBuf(Item item, ByteBuf buf) {
        return ByteBufUtil.writeItem(buf, item);
    }

    protected ItemStack readItemStackFromByteBuf(ByteBuf buf) {
        return ByteBufUtil.readItemStack(buf);
    }

    protected ByteBuf writeItemStackToByteBuf(ByteBuf buf, ItemStack stack) {
        return ByteBufUtil.writeItemStack(buf, stack);
    }

    protected NBTTagCompound readNBTFromByteBuf(ByteBuf buf) {
        return ByteBufUtil.readNBT(buf);
    }

    protected ByteBuf writeNBTToByteBuf(ByteBuf buf, NBTTagCompound tag) {
        return ByteBufUtil.writeNBT(buf, tag);
    }

    protected int[] readIntArrayFromByteBuf(ByteBuf buf) {
        return ByteBufUtil.readIntArray(buf);
    }

    protected ByteBuf writeIntArrayToByteBuf(ByteBuf buf, int[] array) {
        return ByteBufUtil.writeIntArray(buf, array);
    }
}
