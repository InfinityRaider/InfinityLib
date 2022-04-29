package com.infinityraider.infinitylib.world;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

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
     * @return the processors for this structure
     */
    Holder<StructureProcessorList> processors();

    /**
     * @return the placement behaviour of this structure
     */
    StructureTemplatePool.Projection placement();
}
