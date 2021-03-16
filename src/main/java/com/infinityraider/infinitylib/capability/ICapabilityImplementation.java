package com.infinityraider.infinitylib.capability;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface ICapabilityImplementation<C extends ICapabilityProvider, V> {
    Capability<V> getCapability();

    boolean shouldApplyCapability(C carrier);

    V createNewValue(C carrier);

    ResourceLocation getCapabilityKey();

    Class<C> getCarrierClass();
}
