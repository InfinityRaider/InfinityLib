package com.infinityraider.infinitylib.capability;

import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IInfSerializableCapabilityImplementation<C extends ICapabilityProvider, V extends ISerializable> extends IInfCapabilityImplementation<C, V> {
    @Override
    default IInfCapabilityImplementation.Serializer<V> getSerializer() {
        return new IInfCapabilityImplementation.Serializer<V>() {
            @Override
            public CompoundNBT writeToNBT(V object) {
                return object.writeToNBT();
            }

            @Override
            public void readFromNBT(V object, CompoundNBT nbt) {
                object.readFromNBT(nbt);
            }
        };
    }

    @Override
    default void copyData(V from, V to) {
        to.readFromNBT(from.writeToNBT());
    }
}
