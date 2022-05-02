package com.infinityraider.infinitylib.utility.registration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.infinitylib.world.IInfStructure;
import com.infinityraider.infinitylib.world.StructureRegistry;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModStructureRegistry {
    protected ModStructureRegistry() {}

    protected final <P extends StructureProcessor>  StructureProcessorType<P> processor(ResourceLocation id, Supplier<Codec<P>> supplier) {
        return StructureRegistry.getInstance().registerProcessorType(id, supplier::get);
    }

    protected final Holder<StructureProcessorList> processorList(ResourceLocation id, StructureProcessor... processors){
        return this.processorList(id, ImmutableList.copyOf(processors));
    }

    protected final Holder<StructureProcessorList> processorList(ResourceLocation id, List<StructureProcessor> processors){
        return StructureRegistry.getInstance().registerProcessorList(id, processors);
    }

    protected final IInfStructure structure(ResourceLocation id, ResourceLocation targetPool, int weight, Holder<StructureProcessorList> processors, StructureTemplatePool.Projection placement) {
        return this.structure(id, ImmutableSet.of(targetPool), weight, processors, placement);
    }

    protected final IInfStructure structure(ResourceLocation id, Set<ResourceLocation> targetPools, int weight, Holder<StructureProcessorList> processors, StructureTemplatePool.Projection placement) {
       return this.structure(id, targetPools, weight, Either.left(processors), placement);
    }

    protected final IInfStructure structure(ResourceLocation id, ResourceLocation targetPool, int weight, RegistryObject<StructureProcessorList> processors, StructureTemplatePool.Projection placement) {
        return this.structure(id, ImmutableSet.of(targetPool), weight, processors, placement);
    }

    protected final IInfStructure structure(ResourceLocation id, Set<ResourceLocation> targetPools, int weight, RegistryObject<StructureProcessorList> processors, StructureTemplatePool.Projection placement) {
        return this.structure(id, targetPools, weight, Either.right(processors), placement);
    }

    private IInfStructure structure(ResourceLocation id, Set<ResourceLocation> targetPools, int weight, Either<Holder<StructureProcessorList>, RegistryObject<StructureProcessorList>> processors, StructureTemplatePool.Projection placement) {
        IInfStructure structure = new IInfStructure() {
            @Override
            public ResourceLocation id() {
                return id;
            }

            @Override
            public Set<ResourceLocation> targetPools() {
                return targetPools;
            }

            @Override
            public int weight() {
                return weight;
            }

            @Override
            public Holder<StructureProcessorList> processors() {
                return processors.map(
                        holder -> holder,
                        object -> object.getHolder().orElse(ProcessorLists.EMPTY)
                );
            }

            @Override
            public StructureTemplatePool.Projection placement() {
                return placement;
            }
        };
        StructureRegistry.getInstance().registerStructure(structure);
        return structure;
    }
}
