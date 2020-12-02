package com.infinityraider.infinitylib.capability;

import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class CapabilityStorage<V extends ISerializable> implements Capability.IStorage<V> {
    @Override
    public INBT writeNBT(Capability<V> capability, V instance, Direction side) {
        return instance != null ? instance.writeToNBT() : null;
    }

    @Override
    public void readNBT(Capability<V> capability, V instance, Direction side, INBT nbt) {
        if(instance != null && (nbt instanceof CompoundNBT)) {
            instance.readFromNBT((CompoundNBT) nbt);
        }
    }
}