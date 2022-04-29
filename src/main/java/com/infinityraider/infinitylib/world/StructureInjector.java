package com.infinityraider.infinitylib.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.InfinityLib;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.*;
import java.util.stream.Collectors;

public class StructureInjector {
    private static final Map<ResourceLocation, LegacyPoolElement> ELEMENT_CACHE = Maps.newHashMap();

    private final Set<IInfStructure> structures;
    private final ResourceLocation target;

    protected StructureInjector(ResourceLocation target) {
        this.structures = Sets.newIdentityHashSet();
        this.target = target;
    }

    protected void addStructure(IInfStructure structure) {
        this.structures.add(structure);
    }

    protected void inject() {
        // Fetch the current pool
        StructureTemplatePool pool = BuiltinRegistries.TEMPLATE_POOL.get(this.target);
        if (pool == null) {
            InfinityLib.instance.getLogger().error("Could not inject structures into {0}, pool not found", this.target);
            return;
        }

        // Fetch the current list of templates
        List<StructurePoolElement> shuffled = pool.getShuffledTemplates(new Random() {
            // This makes sure the array is not shuffled
            @Override
            public int nextInt(int bound) {
                return bound - 1;
            }
        });

        // Compile into counts
        List<Pair<StructurePoolElement, Integer>> rawTemplates = shuffled.stream()
                .collect(Collectors.toMap(
                        element -> element,
                        element -> 1,
                        Integer::sum
                )).entrySet().stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // Add the new structures
        this.structures.forEach(structure -> rawTemplates.add(new Pair<>(getOrCreatePoolElement(structure), structure.weight())));

        // Register registry override
        int id = BuiltinRegistries.TEMPLATE_POOL.getId(pool);
        ResourceLocation name = pool.getName();
        ((WritableRegistry<StructureTemplatePool>)BuiltinRegistries.TEMPLATE_POOL).registerOrOverride(
                OptionalInt.of(id),
                ResourceKey.create(BuiltinRegistries.TEMPLATE_POOL.key(), pool.getName()),
                new StructureTemplatePool(this.target, name, rawTemplates),
                Lifecycle.stable()
        );
    }

    private static LegacyPoolElement getOrCreatePoolElement(IInfStructure structure) {
        return ELEMENT_CACHE.computeIfAbsent(structure.id(), id -> new LegacyPoolElement(structure));
    }

    private static class LegacyPoolElement extends LegacySinglePoolElement {
        protected LegacyPoolElement(IInfStructure structure) {
            super(Either.left(structure.id()), structure.processors(), structure.placement());
        }
    }
}
