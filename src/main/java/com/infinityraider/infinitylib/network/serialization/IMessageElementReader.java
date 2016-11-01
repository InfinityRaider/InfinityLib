package com.infinityraider.infinitylib.network.serialization;

import io.netty.buffer.ByteBuf;

public interface IMessageElementReader<T> {
    T readData(ByteBuf buf);
}
