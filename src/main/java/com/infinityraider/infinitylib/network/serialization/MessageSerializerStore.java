package com.infinityraider.infinitylib.network.serialization;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

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
        registerMessageSerializer(BlockEntity.class, PacketBufferUtil::writeTileEntity, PacketBufferUtil::readTileEntity);
        registerMessageSerializer(BlockPos.class, PacketBufferUtil::writeBlockPos, PacketBufferUtil::readBlockPos);
        registerMessageSerializer(Block.class, PacketBufferUtil::writeBlock, PacketBufferUtil::readBlock);
        registerMessageSerializer(Item.class, PacketBufferUtil::writeItem, PacketBufferUtil::readItem);
        registerMessageSerializer(ItemStack.class, PacketBufferUtil::writeItemStack, PacketBufferUtil::readItemStack);
        registerMessageSerializer(CompoundTag.class, PacketBufferUtil::writeNBT, PacketBufferUtil::readNBT);
        registerMessageSerializer(Vec3.class, PacketBufferUtil::writeVec3d, PacketBufferUtil::readVec3d);
        registerMessageSerializer(Component.class, PacketBufferUtil::writeTextComponent, PacketBufferUtil::readTextComponent);
        registerMessageSerializer(ResourceKey.class, PacketBufferUtil::writeRegistryKey, PacketBufferUtil::readRegistryKey);
        registerMessageSerializer(MessageSerializerEnum.INSTANCE);
        registerMessageSerializer(MessageSerializerSubClass.TILE_ENTITY);
        registerMessageSerializer(MessageSerializerSubClass.ENTITY);
        registerMessageSerializer(MessageSerializerSubClass.BLOCK);
        registerMessageSerializer(MessageSerializerSubClass.ITEM);
        registerMessageSerializer(MessageSerializerSubClass.TEXT);
        registerMessageSerializer(MessageSerializerArray.INSTANCE);
        registerMessageSerializer(MessageSerializerMap.INSTANCE);
    }
}
