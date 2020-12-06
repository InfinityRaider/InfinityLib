package com.infinityraider.infinitylib.network.serialization;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class PacketBufferUtil {
    public static PacketBuffer writeBoolean(PacketBuffer buf, boolean data) {
        buf.writeBoolean(data);
        return buf;
    }

    public static boolean readBoolean(PacketBuffer buf) {
        return buf.readBoolean();
    }

    public static PacketBuffer writeByte(PacketBuffer buf, byte data) {
        buf.writeByte(data);
        return buf;
    }

    public static byte readByte(PacketBuffer buf) {
        return buf.readByte();
    }

    public static PacketBuffer writeShort(PacketBuffer buf, short data) {
        buf.writeShort(data);
        return buf;
    }

    public static short readShort(PacketBuffer buf) {
        return buf.readShort();
    }

    public static PacketBuffer writeInt(PacketBuffer buf, int data) {
        buf.writeInt(data);
        return buf;
    }

    public static int readInt(PacketBuffer buf) {
        return buf.readInt();
    }

    public static PacketBuffer writeLong(PacketBuffer buf, long data) {
        buf.writeLong(data);
        return buf;
    }

    public static long readLong(PacketBuffer buf) {
        return buf.readLong();
    }

    public static PacketBuffer writeFloat(PacketBuffer buf, float data) {
        buf.writeFloat(data);
        return buf;
    }

    public static float readFloat(PacketBuffer buf) {
        return buf.readFloat();
    }

    public static PacketBuffer writeDouble(PacketBuffer buf, double data) {
        buf.writeDouble(data);
        return buf;
    }

    public static double readDouble(PacketBuffer buf) {
        return buf.readDouble();
    }

    public static PacketBuffer writeChar(PacketBuffer buf, char data) {
        buf.writeChar(data);
        return buf;
    }

    public static char readChar(PacketBuffer buf) {
        return buf.readChar();
    }

    public static PacketBuffer writeString(PacketBuffer buf, String string) {
        buf.writeString(string);
        return buf;
    }
    
    public static String readString(PacketBuffer buf) {
        return buf.readString();
    }

    public static PacketBuffer writeResourceLocation(PacketBuffer buf, ResourceLocation rl) {
        buf.writeResourceLocation(rl);
        return buf;
    }

    public static ResourceLocation readResourceLocation(PacketBuffer buf) {
        return buf.readResourceLocation();
    }

    public static <T> PacketBuffer writeRegistryKey(PacketBuffer buf, RegistryKey<T> key) {
        ResourceLocation parent = key.getRegistryName();
        ResourceLocation name = key.getLocation();
        writeResourceLocation(buf, parent);
        writeResourceLocation(buf, name);
        return buf;
    }

    public static <T> RegistryKey<T> readRegistryKey(PacketBuffer buf) {
        ResourceLocation parent = readResourceLocation(buf);
        ResourceLocation name = readResourceLocation(buf);
        return RegistryKey.getOrCreateKey(RegistryKey.getOrCreateRootKey(parent), name);
    }

    public static PacketBuffer writeEntity(PacketBuffer buf, Entity e) {
        if (e == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(e.getEntityId());
            writeRegistryKey(buf, e.getEntityWorld().getDimensionKey());
        }
        return buf;
    }

    public static Entity readEntity(PacketBuffer buf) {
        int id = buf.readInt();
        if(id < 0) {
            return null;
        }
        RegistryKey<World> dimension = readRegistryKey(buf);
        return InfinityLib.instance.getEntityById(dimension, id);
    }

    public static PacketBuffer writeTileEntity(PacketBuffer buf, TileEntity tile) {
        writeRegistryKey(buf, tile.getWorld().getDimensionKey());
        return writeBlockPos(buf, tile.getPos());
    }

    public static TileEntity readTileEntity(PacketBuffer buf) {
        RegistryKey<World> dimension = readRegistryKey(buf);
        BlockPos pos = readBlockPos(buf);
        World world = InfinityLib.instance.getWorldFromDimension(dimension);
        return world == null ? null : world.getTileEntity(pos);
    }

    public static PacketBuffer writeBlockPos(PacketBuffer buf, BlockPos pos) {
        buf.writeBlockPos(pos);
        return buf;
    }

    public static BlockPos readBlockPos(PacketBuffer buf) {
        return buf.readBlockPos();
    }

    public static PacketBuffer writeBlock(PacketBuffer buf, Block block) {
        return writeRegistryEntry(buf, block);
    }

    public static Block readBlock(PacketBuffer buf) {
        return readRegistryEntry(buf, Block.class);
    }

    public static PacketBuffer writeItem(PacketBuffer buf, Item item) {
        return writeRegistryEntry(buf, item);
    }

    public static Item readItem(PacketBuffer buf) {
        return readRegistryEntry(buf, Item.class);
    }

    public static <T extends IForgeRegistryEntry<T>> PacketBuffer writeRegistryEntry(PacketBuffer buf, T entry) {
        buf.writeRegistryId(entry);
        return buf;
    }

    public static <T extends IForgeRegistryEntry<T>> T readRegistryEntry(PacketBuffer buf, Class<T> clazz) {
        return buf.readRegistryIdSafe(clazz);
    }

    public static PacketBuffer writeItemStack(PacketBuffer buf, ItemStack stack) {
        buf.writeItemStack(stack);
        return buf;
    }

    public static ItemStack readItemStack(PacketBuffer buf) {
        return buf.readItemStack();
    }

    public static PacketBuffer writeNBT(PacketBuffer buf, CompoundNBT tag) {
        buf.writeCompoundTag(tag);
        return buf;
    }

    public static CompoundNBT readNBT(PacketBuffer buf) {
        return buf.readCompoundTag();
    }

    public static PacketBuffer writeVec3d(PacketBuffer buf, Vector3d data) {
        buf.writeDouble(data.x);
        buf.writeDouble(data.y);
        buf.writeDouble(data.z);
        return buf;
    }

    public static Vector3d readVec3d(PacketBuffer buf) {
        return new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static PacketBuffer writeTextComponent(PacketBuffer buf, ITextComponent component) {
        return writeString(buf, ITextComponent.Serializer.toJson(component));
    }

    public static ITextComponent readTextComponent(PacketBuffer buf) {
        return ITextComponent.Serializer.getComponentFromJson(readString(buf));
    }
}
