package com.infinityraider.infinitylib.utility;

import net.minecraft.nbt.CompoundNBT;

public interface ISerializable {
    void readFromNBT(CompoundNBT tag);

    CompoundNBT writeToNBT();
}
