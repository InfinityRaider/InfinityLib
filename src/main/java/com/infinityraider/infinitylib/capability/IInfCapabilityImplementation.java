package com.infinityraider.infinitylib.capability;

import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IInfCapabilityImplementation<C extends ICapabilityProvider, V extends ISerializable> extends ICapabilityImplementation<C, V> {
    Class<V> getCapabilityClass();
}
