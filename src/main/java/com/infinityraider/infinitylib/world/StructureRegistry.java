package com.infinityraider.infinitylib.world;

import com.google.common.collect.Maps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class StructureRegistry {
    private static final StructureRegistry INSTANCE = new StructureRegistry();

    public static StructureRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, StructureInjector> injectors;

    private StructureRegistry() {
        this.injectors = Maps.newHashMap();
    }

    public void registerStructure(IInfStructure structure) {
        structure.targetPools().forEach(target -> this.injectors.computeIfAbsent(target, StructureInjector::new).addStructure(structure));
    }

    public void injectStructures(RegistryAccess registries) {
        this.injectors.values().forEach(injector -> injector.inject(registries));
    }
}
