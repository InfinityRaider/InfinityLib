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

public final class MessageElementWriters {
    private static final Map<Class, IMessageElementWriter> WRITERS = Maps.newHashMap();

    public static <T> void register(Class<T> clazz, IMessageElementWriter<T> writer) {
        if(!WRITERS.containsKey(clazz)) {
            WRITERS.put(clazz, writer);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<IMessageElementWriter<T>> getWriter(T object) {
        return WRITERS.containsKey(object.getClass()) ? Optional.of((IMessageElementWriter<T>) WRITERS.get(object.getClass())) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<IMessageElementWriter<T>> getWriter(Class<T> clazz) {
        return WRITERS.containsKey(clazz) ? Optional.of((IMessageElementWriter<T>) WRITERS.get(clazz)) : Optional.empty();
    }

    public static final IMessageElementWriter<Boolean> BOOLEAN = ByteBuf::writeBoolean;

    public static final IMessageElementWriter<Byte> BYTE = ByteBuf::writeByte;

    public static final IMessageElementWriter<Short> SHORT = ByteBuf::writeShort;

    public static final IMessageElementWriter<Integer> INTEGER = ByteBuf::writeInt;

    public static final IMessageElementWriter<Long> LONG = ByteBuf::writeLong;

    public static final IMessageElementWriter<Float> FLOAT = ByteBuf::writeFloat;

    public static final IMessageElementWriter<Double> DOUBLE = ByteBuf::writeDouble;

    public static final IMessageElementWriter<Character> CHAR = ByteBuf::writeChar;

    public static final IMessageElementWriter<Entity> ENTITY = ByteBufUtil::writeEntity;

    public static final IMessageElementWriter<EntityPlayer> PLAYER = ByteBufUtil::writePlayer;

    public static final IMessageElementWriter<BlockPos> POS = ByteBufUtil::writeBlockPos;

    public static final IMessageElementWriter<Block> BLOCK = ByteBufUtil::writeBlock;

    public static final IMessageElementWriter<Item> ITEM = ByteBufUtil::writeItem;

    public static final IMessageElementWriter<ItemStack> STACK = ByteBufUtil::writeItemStack;

    public static final IMessageElementWriter<NBTTagCompound> NBT = ByteBufUtil::writeNBT;

    static {
        WRITERS.put(Boolean.class, BOOLEAN);
        WRITERS.put(Byte.class, BYTE);
        WRITERS.put(Short.class, SHORT);
        WRITERS.put(Integer.class, INTEGER);
        WRITERS.put(Long.class, LONG);
        WRITERS.put(Float.class, FLOAT);
        WRITERS.put(Double.class, DOUBLE);
        WRITERS.put(Character.class, CHAR);
        WRITERS.put(Entity.class, ENTITY);
        WRITERS.put(EntityPlayer.class, PLAYER);
        WRITERS.put(BlockPos.class, POS);
        WRITERS.put(Block.class, BLOCK);
        WRITERS.put(Item.class, ITEM);
        WRITERS.put(ItemStack.class, STACK);
        WRITERS.put(NBTTagCompound.class, NBT);
    }

    private MessageElementWriters() {}
}
