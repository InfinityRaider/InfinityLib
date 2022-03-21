package com.infinityraider.infinitylib.network.serialization;

import com.infinityraider.infinitylib.InfinityLib;
import com.mojang.math.Vector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class PacketBufferUtil {
    public static FriendlyByteBuf writeBoolean(FriendlyByteBuf buf, boolean data) {
        buf.writeBoolean(data);
        return buf;
    }

    public static boolean readBoolean(FriendlyByteBuf buf) {
        return buf.readBoolean();
    }

    public static FriendlyByteBuf writeByte(FriendlyByteBuf buf, byte data) {
        buf.writeByte(data);
        return buf;
    }

    public static byte readByte(FriendlyByteBuf buf) {
        return buf.readByte();
    }

    public static FriendlyByteBuf writeShort(FriendlyByteBuf buf, short data) {
        buf.writeShort(data);
        return buf;
    }

    public static short readShort(FriendlyByteBuf buf) {
        return buf.readShort();
    }

    public static FriendlyByteBuf writeInt(FriendlyByteBuf buf, int data) {
        buf.writeInt(data);
        return buf;
    }

    public static int readInt(FriendlyByteBuf buf) {
        return buf.readInt();
    }

    public static FriendlyByteBuf writeLong(FriendlyByteBuf buf, long data) {
        buf.writeLong(data);
        return buf;
    }

    public static long readLong(FriendlyByteBuf buf) {
        return buf.readLong();
    }

    public static FriendlyByteBuf writeFloat(FriendlyByteBuf buf, float data) {
        buf.writeFloat(data);
        return buf;
    }

    public static float readFloat(FriendlyByteBuf buf) {
        return buf.readFloat();
    }

    public static FriendlyByteBuf writeDouble(FriendlyByteBuf buf, double data) {
        buf.writeDouble(data);
        return buf;
    }

    public static double readDouble(FriendlyByteBuf buf) {
        return buf.readDouble();
    }

    public static FriendlyByteBuf writeChar(FriendlyByteBuf buf, char data) {
        buf.writeChar(data);
        return buf;
    }

    public static char readChar(FriendlyByteBuf buf) {
        return buf.readChar();
    }

    public static FriendlyByteBuf writeString(FriendlyByteBuf buf, String string) {
        buf.writeUtf(string);
        return buf;
    }
    
    public static String readString(FriendlyByteBuf buf) {
        return buf.readUtf(32767);
    }

    public static FriendlyByteBuf writeResourceLocation(FriendlyByteBuf buf, ResourceLocation rl) {
        buf.writeResourceLocation(rl);
        return buf;
    }

    public static ResourceLocation readResourceLocation(FriendlyByteBuf buf) {
        return buf.readResourceLocation();
    }

    public static <T> FriendlyByteBuf writeRegistryKey(FriendlyByteBuf buf, ResourceKey<T> key) {
        ResourceLocation parent = key.getRegistryName();
        ResourceLocation name = key.location();
        writeResourceLocation(buf, parent);
        writeResourceLocation(buf, name);
        return buf;
    }

    public static <T> ResourceKey<T> readRegistryKey(FriendlyByteBuf buf) {
        ResourceLocation parent = readResourceLocation(buf);
        ResourceLocation name = readResourceLocation(buf);
        return ResourceKey.create(ResourceKey.createRegistryKey(parent), name);
    }

    public static FriendlyByteBuf writeEntity(FriendlyByteBuf buf, Entity e) {
        if (e == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(e.getId());
            writeRegistryKey(buf, e.getLevel().dimension());
        }
        return buf;
    }

    public static Entity readEntity(FriendlyByteBuf buf) {
        int id = buf.readInt();
        if(id < 0) {
            return null;
        }
        ResourceKey<Level> dimension = readRegistryKey(buf);
        return InfinityLib.instance.getEntityById(dimension, id);
    }

    public static FriendlyByteBuf writeTileEntity(FriendlyByteBuf buf, BlockEntity tile) {
        writeRegistryKey(buf, tile.getLevel().dimension());
        return writeBlockPos(buf, tile.getBlockPos());
    }

    public static BlockEntity readTileEntity(FriendlyByteBuf buf) {
        ResourceKey<Level> dimension = readRegistryKey(buf);
        BlockPos pos = readBlockPos(buf);
        Level world = InfinityLib.instance.getWorldFromDimension(dimension);
        return world == null ? null : world.getBlockEntity(pos);
    }

    public static FriendlyByteBuf writeBlockPos(FriendlyByteBuf buf, BlockPos pos) {
        buf.writeBlockPos(pos);
        return buf;
    }

    public static BlockPos readBlockPos(FriendlyByteBuf buf) {
        return buf.readBlockPos();
    }

    public static FriendlyByteBuf writeBlock(FriendlyByteBuf buf, Block block) {
        return writeRegistryEntry(buf, block);
    }

    public static Block readBlock(FriendlyByteBuf buf) {
        return readRegistryEntry(buf, Block.class);
    }

    public static FriendlyByteBuf writeItem(FriendlyByteBuf buf, Item item) {
        return writeRegistryEntry(buf, item);
    }

    public static Item readItem(FriendlyByteBuf buf) {
        return readRegistryEntry(buf, Item.class);
    }

    public static <T extends IForgeRegistryEntry<T>> FriendlyByteBuf writeRegistryEntry(FriendlyByteBuf buf, T entry) {
        buf.writeRegistryId(entry);
        return buf;
    }

    public static <T extends IForgeRegistryEntry<T>> T readRegistryEntry(FriendlyByteBuf buf, Class<T> clazz) {
        return buf.readRegistryIdSafe(clazz);
    }

    public static FriendlyByteBuf writeItemStack(FriendlyByteBuf buf, ItemStack stack) {
        buf.writeItemStack(stack, true);
        return buf;
    }

    public static ItemStack readItemStack(FriendlyByteBuf buf) {
        return buf.readItem();
    }

    public static FriendlyByteBuf writeNBT(FriendlyByteBuf buf, CompoundTag tag) {
        buf.writeNbt(tag);
        return buf;
    }

    public static CompoundTag readNBT(FriendlyByteBuf buf) {
        return buf.readNbt();
    }

    public static FriendlyByteBuf writeVec3d(FriendlyByteBuf buf, Vector3d data) {
        buf.writeDouble(data.x);
        buf.writeDouble(data.y);
        buf.writeDouble(data.z);
        return buf;
    }

    public static Vector3d readVec3d(FriendlyByteBuf buf) {
        return new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static FriendlyByteBuf writeTextComponent(FriendlyByteBuf buf, Component component) {
        buf.writeComponent(component);
        return buf;
    }

    public static Component readTextComponent(FriendlyByteBuf buf) {
        return buf.readComponent();
    }
}
