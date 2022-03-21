package com.infinityraider.infinitylib.network.serialization;

import net.minecraft.network.FriendlyByteBuf;

public interface IMessageReader<T> {
    /**
     * Reads data from a byte buffer
     * @param buf the byte buffer
     * @return the read data
     */
    T readData(FriendlyByteBuf buf);
}
