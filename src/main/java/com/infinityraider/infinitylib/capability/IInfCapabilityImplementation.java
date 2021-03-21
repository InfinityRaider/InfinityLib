package com.infinityraider.infinitylib.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.concurrent.Callable;

public interface IInfCapabilityImplementation<C extends ICapabilityProvider, V> extends ICapabilityImplementation<C, V> {
    Class<V> getCapabilityClass();

    Serializer<V> getSerializer();

    default Callable<? extends V> factory() {
        return () -> null;
    }

    void copyData(V from, V to);

   interface Serializer<V> {
       CompoundNBT writeToNBT(V object);

       void readFromNBT(V object, CompoundNBT nbt);
   }
}
