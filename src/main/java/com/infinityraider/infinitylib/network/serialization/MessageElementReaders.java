package com.infinityraider.infinitylib.network.serialization;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Optional;

public final class MessageElementReaders {
    private static final Map<Class, IMessageElementReader> READERS = Maps.newHashMap();

    public static <T> void register(Class<T> clazz, IMessageElementReader<T> writer) {
        if(!READERS.containsKey(clazz)) {
            READERS.put(clazz, writer);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<IMessageElementReader<T>> getWriter(Class<T> clazz) {
        return READERS.containsKey(clazz) ? Optional.of((IMessageElementReader<T>) READERS.get(clazz)) : Optional.empty();
    }

    public static final IMessageElementReader<Boolean> BOOLEAN = ByteBuf::readBoolean;

    public static final IMessageElementReader<Byte> BYTE = ByteBuf::readByte;

    public static final IMessageElementReader<Short> SHORT = ByteBuf::readShort;

    public static final IMessageElementReader<Integer> INTEGER = ByteBuf::readInt;

    public static final IMessageElementReader<Long> LONG = ByteBuf::readLong;

    public static final IMessageElementReader<Float> FLOAT = ByteBuf::readFloat;

    public static final IMessageElementReader<Double> DOUBLE = ByteBuf::readDouble;

    public static final IMessageElementReader<Character> CHAR = ByteBuf::readChar;

    public static final IMessageElementReader<Entity> ENTITY = ByteBufUtil::readEntity;

    public static final IMessageElementReader<EntityPlayer> PLAYER = ByteBufUtil::readPlayer;

    public static final IMessageElementReader<BlockPos> POS = ByteBufUtil::readBlockPos;

    public static final IMessageElementReader<Block> BLOCK = ByteBufUtil::readBlock;

    public static final IMessageElementReader<Item> ITEM = ByteBufUtil::readItem;

    public static final IMessageElementReader<ItemStack> STACK = ByteBufUtil::readItemStack;

    public static final IMessageElementReader<NBTTagCompound> NBT = ByteBufUtil::readNBT;

    static {
        READERS.put(Boolean.class, BOOLEAN);
        READERS.put(Byte.class, BYTE);
        READERS.put(Short.class, SHORT);
        READERS.put(Integer.class, INTEGER);
        READERS.put(Long.class, LONG);
        READERS.put(Float.class, FLOAT);
        READERS.put(Double.class, DOUBLE);
        READERS.put(Character.class, CHAR);
        READERS.put(Entity.class, ENTITY);
        READERS.put(EntityPlayer.class, PLAYER);
        READERS.put(BlockPos.class, POS);
        READERS.put(Block.class, BLOCK);
        READERS.put(Item.class, ITEM);
        READERS.put(ItemStack.class, STACK);
        READERS.put(NBTTagCompound.class, NBT);
    }

    private MessageElementReaders() {}
}
