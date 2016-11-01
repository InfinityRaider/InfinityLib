package com.infinityraider.infinitylib.network.serialization;

import io.netty.buffer.ByteBuf;

public interface IMessageElementWriter<T> {
    void writeData(ByteBuf buf, T data);
}
