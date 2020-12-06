package com.infinityraider.infinitylib.modules.synchronizedeffects;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.capability.ICapabilityImplementation;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.network.INetworkWrapper;

import java.util.List;

public class ModuleSynchronizedEffects extends Module {
    private static final ModuleSynchronizedEffects INSTANCE = new ModuleSynchronizedEffects();

    public static final CapabilityEffectTracker CAPABILITY = CapabilityEffectTracker.getInstance();
    public static final EffectHandler HANDLER = EffectHandler.getInstance();

    public static ModuleSynchronizedEffects getInstance() {
        return INSTANCE;
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageSyncEffects.class);
    }

    @Override
    public List<Object> getCommonEventHandlers() {
        return ImmutableList.of(HANDLER);
    }

    @Override
    public List<ICapabilityImplementation> getCapabilities() {
        return ImmutableList.of(CAPABILITY);
    }
}
