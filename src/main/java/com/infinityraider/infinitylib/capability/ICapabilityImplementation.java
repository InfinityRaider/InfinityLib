package com.infinityraider.infinitylib.capability;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

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
}
