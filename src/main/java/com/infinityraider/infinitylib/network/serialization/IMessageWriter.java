package com.infinityraider.infinitylib.network.serialization;

import net.minecraft.network.FriendlyByteBuf;

public interface IMessageWriter<T> {
    /**
     * writes data to a byte buffer
     * @param buf the byte buffer
     * @param data the data to write
     */
    void writeData(FriendlyByteBuf buf, T data);
}
