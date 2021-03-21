package com.infinityraider.infinitylib.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class CapabilityStorage<V> implements Capability.IStorage<V> {
    private final IInfCapabilityImplementation.Serializer<V> serializer;

    protected CapabilityStorage(IInfCapabilityImplementation.Serializer<V> serializer) {
        this.serializer = serializer;
    }

    @Override
    public INBT writeNBT(Capability<V> capability, V instance, Direction side) {
        return instance != null ? this.serializer.writeToNBT(instance) : null;
    }

    @Override
    public void readNBT(Capability<V> capability, V instance, Direction side, INBT nbt) {
        if(instance != null && (nbt instanceof CompoundNBT)) {
            this.serializer.readFromNBT(instance, (CompoundNBT) nbt);
        }
    }
}