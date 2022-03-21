package com.infinityraider.infinitylib.capability;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ICapabilityImplementation<C extends ICapabilityProvider, V> {
    Capability<V> getCapability();

    boolean shouldApplyCapability(C carrier);

    V createNewValue(C carrier);

    ResourceLocation getCapabilityKey();

    Class<C> getCarrierClass();

    default LazyOptional<V> getCapability(C carrier) {
        return carrier == null ? LazyOptional.empty() : carrier.getCapability(this.getCapability());
    }

    default LazyOptional<V> getCapability(C carrier, @Nullable Direction dir) {
        return carrier.getCapability(this.getCapability(), dir);
    }

    default ICapabilityProvider createProvider(C carrier) {
        LazyOptional<V> value = LazyOptional.of(() -> this.createNewValue(carrier));
        Capability<V> capability = this.getCapability();
        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == capability) {
                    return value.cast();
                } else {
                    return LazyOptional.empty();
                }
            }
        };
    }
}
