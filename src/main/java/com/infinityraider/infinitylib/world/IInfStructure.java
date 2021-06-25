package com.infinityraider.infinitylib.world;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;

import java.util.Set;

/**
 * Wrapper class to inject structures into vanilla's world gen
 */
public interface IInfStructure {
    /**
     * @return the Resource Location for the structure, must match the nbt file in data/<domain>/structures
     */
    ResourceLocation id();

    /**
     * @return the target pools into which this structure is to be injected
     */
    Set<ResourceLocation> targetPools();

    /**
     * @return the spawn weight of this structure
     */
    int weight();

    /**
     * @return the placement behaviour of this structure
     */
    JigsawPattern.PlacementBehaviour placement();
}
