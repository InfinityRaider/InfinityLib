package com.infinityraider.infinitylib.network.serialization;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class MessageSerializerSubClass<C extends P, P> implements IMessageSerializer<C> {
    public static final IMessageSerializer<TileEntity> TILE_ENTITY = new MessageSerializerSubClass<>(TileEntity.class);
    public static final IMessageSerializer<Entity> ENTITY = new MessageSerializerSubClass<>(Entity.class);
    public static final IMessageSerializer<Block> BLOCK = new MessageSerializerSubClass<>(Block.class);
    public static final IMessageSerializer<Item> ITEM = new MessageSerializerSubClass<>(Item.class);

    private final Class<P> parentClass;

    private MessageSerializerSubClass(Class<P> parentClass) {
        this.parentClass = parentClass;
    }

    @Override
    public boolean accepts(Class<C> childClass) {
        return MessageSerializerStore.getMessageSerializer(parentClass).isPresent() && parentClass.isAssignableFrom(childClass);
    }

    @Override
    public IMessageWriter<C> getWriter(Class<C> childClass) {
        IMessageSerializer<P> serializer = MessageSerializerStore.getMessageSerializer(parentClass).get();
        IMessageWriter<P> writer = serializer.getWriter(parentClass);
        return (buf, data) -> writer.writeData(buf, (P) data);
    }

    @Override
    public IMessageReader<C> getReader(Class<C> childClass) {
        IMessageSerializer<P> serializer = MessageSerializerStore.getMessageSerializer(parentClass).get();
        IMessageReader<P> reader = serializer.getReader(parentClass);
        return (buf) -> {
            P obj = reader.readData(buf);
            return childClass.isInstance(obj) ? childClass.cast(obj) : null;
        };
    }
}
