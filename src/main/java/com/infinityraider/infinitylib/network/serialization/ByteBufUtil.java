package com.infinityraider.infinitylib.network.serialization;

import com.infinityraider.infinitylib.InfinityLib;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ByteBufUtil {
    public static ByteBuf writeString(ByteBuf buf, String string) {
        ByteBufUtils.writeUTF8String(buf, string);
        return buf;
    }
    
    public static String readString(ByteBuf buf) {
        return ByteBufUtils.readUTF8String(buf);
    }

    public static ByteBuf writePlayer(ByteBuf buf, EntityPlayer player) {
        writeEntity(buf, player);
        return buf;
    }

    public static EntityPlayer readPlayer(ByteBuf buf) {
        Entity entity = readEntity(buf);
        return (entity instanceof EntityPlayer) ? (EntityPlayer) entity : null;
    }

    public static ByteBuf writeEntity(ByteBuf buf, Entity e) {
        if (e == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(e.getEntityId());
            buf.writeInt(e.worldObj.provider.getDimension());
        }
        return buf;
    }

    public static Entity readEntity(ByteBuf buf) {
        int id = buf.readInt();
        if(id < 0) {
            return null;
        }
        int dimension = buf.readInt();
        return InfinityLib.proxy.getEntityById(dimension, id);
    }

    public static ByteBuf writeBlockPos(ByteBuf buf, BlockPos pos) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        return buf;
    }

    public static BlockPos readBlockPos(ByteBuf buf) {
        return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static ByteBuf writeBlock(ByteBuf buf, Block block) {
        String blockName = block == null ? "null" : Block.REGISTRY.getNameForObject(block).toString();
        buf.writeInt(blockName.length());
        buf.writeBytes(blockName.getBytes());
        return buf;
    }

    public static Block readBlock(ByteBuf buf) {
        int itemNameLength = buf.readInt();
        String itemName = new String(buf.readBytes(itemNameLength).array());
        return Block.REGISTRY.getObject(new ResourceLocation(itemName));
    }

    public static ByteBuf writeItem(ByteBuf buf, Item item) {
        String itemName = item == null ? "null" : Item.REGISTRY.getNameForObject(item).toString();
        buf.writeInt(itemName.length());
        buf.writeBytes(itemName.getBytes());
        return buf;
    }

    public static Item readItem(ByteBuf buf) {
        int itemNameLength = buf.readInt();
        String itemName = new String(buf.readBytes(itemNameLength).array());
        return Item.REGISTRY.getObject(new ResourceLocation(itemName));
    }

    public static ByteBuf writeItemStack(ByteBuf buf, ItemStack stack) {
        ByteBufUtils.writeItemStack(buf, stack);
        return buf;
    }

    public static ItemStack readItemStack(ByteBuf buf) {
        return ByteBufUtils.readItemStack(buf);
    }

    public static ByteBuf writeNBT(ByteBuf buf, NBTTagCompound tag) {
        ByteBufUtils.writeTag(buf, tag);
        return buf;
    }

    public static NBTTagCompound readNBT(ByteBuf buf) {
        return ByteBufUtils.readTag(buf);
    }

    public static ByteBuf writeIntArray(ByteBuf buf, int[] array) {
        buf.writeInt(array.length);
        for(int i = 0; i < array.length; i++) {
            buf.writeInt(array[i]);
        }
        return buf;
    }

    public static int[] readIntArray(ByteBuf buf) {
        int amount = buf.readInt();
        int[] array = new int[amount];
        for(int i = 0; i < amount; i++) {
            array[i] = buf.readInt();
        }
        return array;
    }
}
