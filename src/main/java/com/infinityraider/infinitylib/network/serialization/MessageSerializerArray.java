package com.infinityraider.infinitylib.network.serialization;

import com.google.common.collect.Lists;

import java.lang.reflect.Array;
import java.util.List;

@SuppressWarnings("OptionalGetWithoutIsPresent, unchecked")
public class MessageSerializerArray<T> implements IMessageSerializer<T[]> {
    public static final IMessageSerializer INSTANCE = new MessageSerializerArray();

    private MessageSerializerArray() {}

    @Override
    public boolean accepts(Class<T[]> clazz) {
        return clazz.isArray() && MessageSerializerStore.getMessageSerializer(clazz.getComponentType()).isPresent();
    }

    @Override
    public IMessageWriter<T[]> getWriter(Class<T[]> clazz) {
        IMessageSerializer<T> element =  MessageSerializerStore.getMessageSerializer((Class<T>) clazz.getComponentType()).get();
        IMessageWriter<T> writer = element.getWriter((Class<T>) clazz.getComponentType());
        return (buf, data) -> {
            buf.writeInt(data.length);
            for (T someData : data) {
                writer.writeData(buf, someData);
            }
        };
    }

    @Override
    public IMessageReader<T[]> getReader(Class<T[]> clazz) {
        IMessageSerializer<T> element =  MessageSerializerStore.getMessageSerializer((Class<T>) clazz.getComponentType()).get();
        IMessageReader<T> reader = element.getReader((Class<T>) clazz.getComponentType());
        return (buf) -> {
            int size = buf.readInt();
            List<T> list = Lists.newArrayList();
            for(int i = 0; i < size; i++) {
                list.add(reader.readData(buf));
            }
            return list.toArray((T[]) Array.newInstance(clazz, size));
        };
    }
}
