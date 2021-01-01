package com.infinityraider.infinitylib.network.serialization;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public final class MessageSerializerStore {
    private static final Set<IMessageSerializer> SERIALIZERS = Sets.newHashSet();

    public static <T> Optional<IMessageSerializer<T>> getMessageSerializer(Class<T> clazz) {
        for (IMessageSerializer serializer : SERIALIZERS) {
            if (serializer.accepts(clazz)) {
                return Optional.of(serializer);
            }
        }
        return Optional.empty();
    }

    public static <T> void registerMessageSerializer(Class<T> clazz, IMessageWriter<T> writer, IMessageReader<T> reader) {
        registerMessageSerializer(MessageSerializerGeneric.createSerializer(clazz, writer, reader));
    }

    public static <T> void registerMessageSerializer(IMessageSerializer<T> serializer) {
        SERIALIZERS.add(serializer);
    }

    static {
        registerMessageSerializer(boolean.class, PacketBufferUtil::writeBoolean, PacketBufferUtil::readBoolean);
        registerMessageSerializer(Boolean.class, PacketBufferUtil::writeBoolean, PacketBufferUtil::readBoolean);
        registerMessageSerializer(byte.class, PacketBufferUtil::writeByte, PacketBufferUtil::readByte);
        registerMessageSerializer(Byte.class, PacketBufferUtil::writeByte, PacketBufferUtil::readByte);
        registerMessageSerializer(short.class, PacketBufferUtil::writeShort, PacketBufferUtil::readShort);
        registerMessageSerializer(Short.class, PacketBufferUtil::writeShort, PacketBufferUtil::readShort);
        registerMessageSerializer(int.class, PacketBufferUtil::writeInt, PacketBufferUtil::readInt);
        registerMessageSerializer(Integer.class, PacketBufferUtil::writeInt, PacketBufferUtil::readInt);
        registerMessageSerializer(long.class, PacketBufferUtil::writeLong, PacketBufferUtil::readLong);
        registerMessageSerializer(Long.class, PacketBufferUtil::writeLong, PacketBufferUtil::readLong);
        registerMessageSerializer(float.class, PacketBufferUtil::writeFloat, PacketBufferUtil::readFloat);
        registerMessageSerializer(Float.class, PacketBufferUtil::writeFloat, PacketBufferUtil::readFloat);
        registerMessageSerializer(double.class, PacketBufferUtil::writeDouble, PacketBufferUtil::readDouble);
        registerMessageSerializer(Double.class, PacketBufferUtil::writeDouble, PacketBufferUtil::readDouble);
        registerMessageSerializer(char.class, PacketBufferUtil::writeChar, PacketBufferUtil::readChar);
        registerMessageSerializer(Character.class, PacketBufferUtil::writeChar, PacketBufferUtil::readChar);
        registerMessageSerializer(String.class, PacketBufferUtil::writeString, PacketBufferUtil::readString);
        registerMessageSerializer(Entity.class, PacketBufferUtil::writeEntity, PacketBufferUtil::readEntity);
        registerMessageSerializer(TileEntity.class, PacketBufferUtil::writeTileEntity, PacketBufferUtil::readTileEntity);
        registerMessageSerializer(BlockPos.class, PacketBufferUtil::writeBlockPos, PacketBufferUtil::readBlockPos);
        registerMessageSerializer(Block.class, PacketBufferUtil::writeBlock, PacketBufferUtil::readBlock);
        registerMessageSerializer(Item.class, PacketBufferUtil::writeItem, PacketBufferUtil::readItem);
        registerMessageSerializer(ItemStack.class, PacketBufferUtil::writeItemStack, PacketBufferUtil::readItemStack);
        registerMessageSerializer(CompoundNBT.class, PacketBufferUtil::writeNBT, PacketBufferUtil::readNBT);
        registerMessageSerializer(Vector3d.class, PacketBufferUtil::writeVec3d, PacketBufferUtil::readVec3d);
        registerMessageSerializer(ITextComponent.class, PacketBufferUtil::writeTextComponent, PacketBufferUtil::readTextComponent);
        registerMessageSerializer(RegistryKey.class, PacketBufferUtil::writeRegistryKey, PacketBufferUtil::readRegistryKey);
        registerMessageSerializer(MessageSerializerEnum.INSTANCE);
        registerMessageSerializer(MessageSerializerSubClass.TILE_ENTITY);
        registerMessageSerializer(MessageSerializerSubClass.ENTITY);
        registerMessageSerializer(MessageSerializerSubClass.BLOCK);
        registerMessageSerializer(MessageSerializerSubClass.ITEM);
        registerMessageSerializer(MessageSerializerSubClass.TEXT);
        registerMessageSerializer(MessageSerializerArray.INSTANCE);
    }
}
