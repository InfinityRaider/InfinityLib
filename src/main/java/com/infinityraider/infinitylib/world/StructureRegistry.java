package com.infinityraider.infinitylib.world;

import com.google.common.collect.Maps;
import net.minecraft.data.worldgen.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

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

    public void injectStructures() {
        bootstrap();
        this.injectors.values().forEach(StructureInjector::inject);
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
        NoiseData.bootstrap();
        PillagerOutpostPools.bootstrap();
        SavannaVillagePools.bootstrap();
        SnowyVillagePools.bootstrap();
        StructureFeature.bootstrap();
        TaigaVillagePools.bootstrap();
        VillagePools.bootstrap();
    }
}
