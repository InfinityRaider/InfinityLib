package com.infinityraider.infinitylib.network.serialization;

import net.minecraft.network.PacketBuffer;

public interface IMessageWriter<T> {
    /**
     * writes data to a byte buffer
     * @param buf the byte buffer
     * @param data the data to write
     */
    void writeData(PacketBuffer buf, T data);
}
