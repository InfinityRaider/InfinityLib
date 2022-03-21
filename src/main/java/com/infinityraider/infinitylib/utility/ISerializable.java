package com.infinityraider.infinitylib.utility;

import net.minecraft.nbt.CompoundTag;

public interface ISerializable {
    void readFromNBT(CompoundTag tag);

    CompoundTag writeToNBT();
}
