package com.infinityraider.infinitylib.modules;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.capability.ICapabilityImplementation;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public abstract class Module implements Comparable<Module> {
    private static final HashSet<Module> activeModules = new HashSet<>();

    public static List<Module> getActiveModules() {
        return ImmutableList.copyOf(activeModules);
    }

    private boolean active;

    protected Module() {
        this.active = false;
    }

    public final void activate() {
        if(!this.active) {
            this.active = true;
            activeModules.add(this);
            this.requiredModules().forEach(Module::activate);
        }
    }

    public List<Module> requiredModules() {
        return ImmutableList.of();
    }

    public void registerMessages(INetworkWrapper wrapper) {}

    public List<Object> getCommonEventHandlers() {
        return Collections.emptyList();
    }

    @OnlyIn(Dist.CLIENT)
    public List<Object> getClientEventHandlers() {
        return Collections.emptyList();
    }

    public List<ICapabilityImplementation<?,?>> getCapabilities() {
        return Collections.emptyList();
    }

    public void init() {}

    @OnlyIn(Dist.CLIENT)
    public void initClient() {}

    public void postInit() {}

    @OnlyIn(Dist.CLIENT)
    public void postInitClient() {}

    @Override
    public int compareTo(Module other) {
        return this.getClass().getName().compareTo(other.getClass().getName());
    }
}
