package com.infinityraider.infinitylib.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IInfCapabilityImplementation<C extends ICapabilityProvider, V> extends ICapabilityImplementation<C, V> {
    Class<V> getCapabilityClass();

    Serializer<V> getSerializer();

    void copyData(V from, V to);

    @Override
    default CapabilityProvider<V> createProvider(C carrier) {
        return new CapabilityProvider<>(this::getCapability, this.getSerializer(), this.createNewValue(carrier));
    }

    interface Serializer<V> {
       CompoundTag writeToNBT(V object);

       void readFromNBT(V object, CompoundTag nbt);
   }
}
