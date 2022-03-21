package com.infinityraider.infinitylib.capability;

import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation.Serializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public interface IInfSerializableCapabilityImplementation<C extends ICapabilityProvider, V extends Serializable<V>> extends IInfCapabilityImplementation<C, V> {
    @Override
    default IInfCapabilityImplementation.Serializer<V> getSerializer() {
        return new IInfCapabilityImplementation.Serializer<>() {
            @Override
            public CompoundTag writeToNBT(V object) {
                return object.serializeNBT();
            }

            @Override
            public void readFromNBT(V object, CompoundTag nbt) {
                object.deserializeNBT(nbt);
            }
        };
    }

    @Override
    default void copyData(V from, V to) {
        to.copyDataFrom(from);
    }

    interface Serializable<F> extends INBTSerializable<CompoundTag> {
        void copyDataFrom(F from);
    }
}
