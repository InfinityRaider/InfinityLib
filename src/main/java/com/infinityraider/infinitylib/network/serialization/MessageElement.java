package com.infinityraider.infinitylib.network.serialization;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MessageElement<T> {
    private static Map<Class, MessageElement> ELEMENTS = Maps.newHashMap();

    private IMessageElementWriter<T> writer;
    private IMessageElementReader<T> reader;

    private MessageElement(IMessageElementWriter<T> writer, IMessageElementReader<T> reader) {
        this.writer = writer;
        this.reader = reader;
    }

    public MessageElement<T> writeToByteBuf(ByteBuf buf, T data) {
        this.writer.writeData(buf, data);
        return this;
    }

    public T readFromByteBuf(ByteBuf buf) {
        return this.reader.readData(buf);
    }

    public static Optional<MessageElement> getMessageElement(Field field) {
        Class clazz = field.getType();
        if(ELEMENTS.containsKey(clazz)) {
            return Optional.of(ELEMENTS.get(clazz));
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<MessageElement<T>> getMessageElement(Class<T> clazz) {
        if(ELEMENTS.containsKey(clazz)) {
            return Optional.of(ELEMENTS.get(clazz));
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void registerElement(Class<T> clazz, IMessageElementWriter<T> writer, IMessageElementReader<T> reader) {
        if(!ELEMENTS.containsKey(clazz)) {
            ELEMENTS.put(clazz, new MessageElement(writer, reader));
            if(!clazz.isArray()) {
                registerArrayForClass(clazz);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void registerArrayForClass(Class<T> clazz) {
        Optional<MessageElement<T>> optional = getMessageElement(clazz);
        if(optional.isPresent()) {
            Class<T[]> arrayClass = (Class<T[]>) Array.newInstance(clazz, 0).getClass();
            MessageElement<T> element = optional.get();
            IMessageElementWriter<T[]> writer = (buf, data) -> {
                buf.writeInt(data.length);
                for (T someData : data) {
                    element.writeToByteBuf(buf, someData);
                }
            };
            IMessageElementReader<T[]> reader = (buf) -> {
                int size = buf.readInt();
                List<T> list = Lists.newArrayList();
                for(int i = 0; i < size; i++) {
                    list.add(element.readFromByteBuf(buf));
                }
                return list.toArray((T[]) Array.newInstance(clazz, size));
            };
            registerElement(arrayClass, writer, reader);
        }
    }

    static {
        registerElement(boolean.class, ByteBufUtil::writeBoolean, ByteBufUtil::readBoolean);
        registerElement(Boolean.class, ByteBufUtil::writeBoolean, ByteBufUtil::readBoolean);
        registerElement(byte.class, ByteBufUtil::writeByte, ByteBufUtil::readByte);
        registerElement(Byte.class, ByteBufUtil::writeByte, ByteBufUtil::readByte);
        registerElement(short.class, ByteBufUtil::writeShort, ByteBufUtil::readShort);
        registerElement(Short.class, ByteBufUtil::writeShort, ByteBufUtil::readShort);
        registerElement(int.class, ByteBufUtil::writeInt, ByteBufUtil::readInt);
        registerElement(Integer.class, ByteBufUtil::writeInt, ByteBufUtil::readInt);
        registerElement(long.class, ByteBufUtil::writeLong, ByteBufUtil::readLong);
        registerElement(Long.class, ByteBufUtil::writeLong, ByteBufUtil::readLong);
        registerElement(float.class, ByteBufUtil::writeFloat, ByteBufUtil::readFloat);
        registerElement(Float.class, ByteBufUtil::writeFloat, ByteBufUtil::readFloat);
        registerElement(double.class, ByteBufUtil::writeDouble, ByteBufUtil::readDouble);
        registerElement(Double.class, ByteBufUtil::writeDouble, ByteBufUtil::readDouble);
        registerElement(char.class, ByteBufUtil::writeChar, ByteBufUtil::readChar);
        registerElement(Character.class, ByteBufUtil::writeChar, ByteBufUtil::readChar);
        registerElement(String.class, ByteBufUtil::writeString, ByteBufUtil::readString);
        registerElement(Entity.class, ByteBufUtil::writeEntity, ByteBufUtil::readEntity);
        registerElement(EntityPlayer.class, ByteBufUtil::writePlayer, ByteBufUtil::readPlayer);
        registerElement(TileEntity.class, ByteBufUtil::writeTileEntity, ByteBufUtil::readTileEntity);
        registerElement(BlockPos.class, ByteBufUtil::writeBlockPos, ByteBufUtil::readBlockPos);
        registerElement(Block.class, ByteBufUtil::writeBlock, ByteBufUtil::readBlock);
        registerElement(Item.class, ByteBufUtil::writeItem, ByteBufUtil::readItem);
        registerElement(ItemStack.class, ByteBufUtil::writeItemStack, ByteBufUtil::readItemStack);
        registerElement(NBTTagCompound.class, ByteBufUtil::writeNBT, ByteBufUtil::readNBT);
    }
}
