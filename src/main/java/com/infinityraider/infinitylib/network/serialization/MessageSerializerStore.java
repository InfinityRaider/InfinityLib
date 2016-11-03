package com.infinityraider.infinitylib.network.serialization;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public final class MessageSerializerStore {
    private static final Map<Class, MessageElement> ELEMENTS = Maps.newHashMap();
    private static final Set<IMessageSerializer> SERIALIZERS = Sets.newHashSet();

    public static Optional<MessageElement> getMessageSerializer(Field field) {
        Class clazz = field.getType();
        if(ELEMENTS.containsKey(clazz)) {
            return Optional.of(ELEMENTS.get(clazz));
        } else {
            return createAndRegisterNewElement(field);
        }
    }

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

    private static Optional<MessageElement> createAndRegisterNewElement(Field field) {
        Class clazz = field.getType();
        Optional<IMessageSerializer> serializer = getMessageSerializer(clazz);
        if (serializer.isPresent()) {
            MessageElement element = new MessageElement(field, serializer.get().getWriter(clazz), serializer.get().getReader(clazz));
            ELEMENTS.put(clazz, element);
            return Optional.of(element);
        } else {
            return Optional.empty();
        }
    }

    static {
        registerMessageSerializer(boolean.class, ByteBufUtil::writeBoolean, ByteBufUtil::readBoolean);
        registerMessageSerializer(Boolean.class, ByteBufUtil::writeBoolean, ByteBufUtil::readBoolean);
        registerMessageSerializer(byte.class, ByteBufUtil::writeByte, ByteBufUtil::readByte);
        registerMessageSerializer(Byte.class, ByteBufUtil::writeByte, ByteBufUtil::readByte);
        registerMessageSerializer(short.class, ByteBufUtil::writeShort, ByteBufUtil::readShort);
        registerMessageSerializer(Short.class, ByteBufUtil::writeShort, ByteBufUtil::readShort);
        registerMessageSerializer(int.class, ByteBufUtil::writeInt, ByteBufUtil::readInt);
        registerMessageSerializer(Integer.class, ByteBufUtil::writeInt, ByteBufUtil::readInt);
        registerMessageSerializer(long.class, ByteBufUtil::writeLong, ByteBufUtil::readLong);
        registerMessageSerializer(Long.class, ByteBufUtil::writeLong, ByteBufUtil::readLong);
        registerMessageSerializer(float.class, ByteBufUtil::writeFloat, ByteBufUtil::readFloat);
        registerMessageSerializer(Float.class, ByteBufUtil::writeFloat, ByteBufUtil::readFloat);
        registerMessageSerializer(double.class, ByteBufUtil::writeDouble, ByteBufUtil::readDouble);
        registerMessageSerializer(Double.class, ByteBufUtil::writeDouble, ByteBufUtil::readDouble);
        registerMessageSerializer(char.class, ByteBufUtil::writeChar, ByteBufUtil::readChar);
        registerMessageSerializer(Character.class, ByteBufUtil::writeChar, ByteBufUtil::readChar);
        registerMessageSerializer(String.class, ByteBufUtil::writeString, ByteBufUtil::readString);
        registerMessageSerializer(Entity.class, ByteBufUtil::writeEntity, ByteBufUtil::readEntity);
        registerMessageSerializer(TileEntity.class, ByteBufUtil::writeTileEntity, ByteBufUtil::readTileEntity);
        registerMessageSerializer(BlockPos.class, ByteBufUtil::writeBlockPos, ByteBufUtil::readBlockPos);
        registerMessageSerializer(Block.class, ByteBufUtil::writeBlock, ByteBufUtil::readBlock);
        registerMessageSerializer(Item.class, ByteBufUtil::writeItem, ByteBufUtil::readItem);
        registerMessageSerializer(ItemStack.class, ByteBufUtil::writeItemStack, ByteBufUtil::readItemStack);
        registerMessageSerializer(NBTTagCompound.class, ByteBufUtil::writeNBT, ByteBufUtil::readNBT);
        registerMessageSerializer(MessageSerializerSubClass.TILE_ENTITY);
        registerMessageSerializer(MessageSerializerSubClass.ENTITY);
        registerMessageSerializer(MessageSerializerSubClass.BLOCK);
        registerMessageSerializer(MessageSerializerSubClass.ITEM);
        registerMessageSerializer(MessageSerializerArray.INSTANCE);
    }
}
