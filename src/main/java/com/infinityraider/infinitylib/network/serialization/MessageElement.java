package com.infinityraider.infinitylib.network.serialization;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.network.MessageBase;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public class MessageElement<T> {
    private final Field field;
    private final IMessageWriter<T> writer;
    private final IMessageReader<T> reader;

    MessageElement(Field field, IMessageWriter<T> writer, IMessageReader<T> reader) {
        this.field = field;
        this.field.setAccessible(true);
        this.writer = writer;
        this.reader = reader;
    }

    public void writeToByteBuf(ByteBuf buf, MessageBase msg) {
        T data = null;
        try {
            data = (T) this.field.get(msg);
        } catch (Exception e) {
            InfinityLib.instance.getLogger().error("Failed getting field data");
            InfinityLib.instance.getLogger().error(e.toString());
        }
        if(data != null) {
            ByteBufUtil.writeBoolean(buf, true);
            this.writer.writeData(buf, data);
        } else {
            ByteBufUtil.writeBoolean(buf, false);
        }
    }

    public void readFromByteBuf(ByteBuf buf, MessageBase msg) {
        boolean shouldRead = buf.readBoolean();
        if (shouldRead) {
            T data = this.reader.readData(buf);
            if(data != null) {
                try {
                    this.field.set(msg, data);
                } catch(Exception e) {
                    InfinityLib.instance.getLogger().error("Failed setting field data");
                    InfinityLib.instance.getLogger().error(e.toString());
                }
            } else {
                InfinityLib.instance.getLogger().error("Object was null, did not set field " + this.field.getName());
            }
        }
    }
}
