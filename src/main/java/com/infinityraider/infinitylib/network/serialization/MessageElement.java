package com.infinityraider.infinitylib.network.serialization;

import io.netty.buffer.ByteBuf;

import java.util.Optional;

public class MessageElement<T> {
    private T data;
    private IMessageElementReader<T> reader;
    private IMessageElementWriter<T> writer;

    public MessageElement() {
        super();
    }

    public MessageElement(Class<T> clazz) {
        this();
        Optional<IMessageElementReader<T>> reader = MessageElementReaders.getReader(clazz);
        if(reader.isPresent()) {
            this.reader = reader.get();
        }
        Optional<IMessageElementWriter<T>> writer = MessageElementWriters.getWriter(clazz);
        if(writer.isPresent()) {
            this.writer = writer.get();
        }
    }

    public MessageElement(T data) {
        this();
        this.data = data;
        Optional<IMessageElementReader<T>> reader = MessageElementReaders.getReader(data);
        if(reader.isPresent()) {
            this.reader = reader.get();
        }
        Optional<IMessageElementWriter<T>> writer = MessageElementWriters.getWriter(data);
        if(writer.isPresent()) {
            this.writer = writer.get();
        }
    }

    public T getData() {
        return this.data;
    }

    public MessageElement<T> readFromByteBuf(ByteBuf buf) {
        this.data = this.reader.readData(buf);
        return this;
    }

    public MessageElement<T> writeToByteBuf(ByteBuf buf) {
        this.writer.writeData(buf, this.getData());
        return this;
    }
}
