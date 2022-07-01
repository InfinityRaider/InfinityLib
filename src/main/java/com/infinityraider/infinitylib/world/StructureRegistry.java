package com.infinityraider.infinitylib.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class StructureRegistry {
    private static final StructureRegistry INSTANCE = new StructureRegistry();

    public static StructureRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, StructureInjector> injectors;
    private final Set<Tuple<ResourceLocation, StructureProcessorType<? extends StructureProcessor>>> processors;

    private StructureRegistry() {
        this.injectors = Maps.newHashMap();
        this.processors = Sets.newIdentityHashSet();
    }

    public Holder<StructureProcessorList> registerProcessorList(ResourceLocation id, List<StructureProcessor> processors) {
        return BuiltinRegistries.register(BuiltinRegistries.PROCESSOR_LIST, id, new StructureProcessorList(processors));
    }

    public <P extends StructureProcessor> StructureProcessorType<P> registerProcessorType(ResourceLocation id, StructureProcessorType<P> type) {
        this.processors.add(new Tuple<>(id, type));
        return type;
    }

    public void registerStructure(IInfStructure structure) {
        // add the structure and processors to the registration pool
        structure.targetPools().forEach(target -> this.injectors.computeIfAbsent(target, StructureInjector::new).addStructure(structure));
    }

    public void injectStructures() {
        bootstrap();
        this.processors.forEach(t -> Registry.register(Registry.STRUCTURE_PROCESSOR, t.getA(), t.getB()));
        this.injectors.values().forEach(StructureInjector::inject);
        this.processors.clear();
        this.injectors.clear();
    }

    // Makes sure all pools are statically initialized
    private static void bootstrap() {
        BastionBridgePools.bootstrap();
        BastionHoglinStablePools.bootstrap();
        BastionHousingUnitsPools.bootstrap();
        BastionPieces.bootstrap();
        BastionSharedPools.bootstrap();
        BastionSharedPools.bootstrap();
        BastionTreasureRoomPools.bootstrap();
        DesertVillagePools.bootstrap();
        PillagerOutpostPools.bootstrap();
        SavannaVillagePools.bootstrap();
        SnowyVillagePools.bootstrap();
        StructureFeature.bootstrap();
        TaigaVillagePools.bootstrap();
        VillagePools.bootstrap();
    }
}
