package com.infinityraider.infinitylib.utility.registration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.world.IInfStructure;
import com.infinityraider.infinitylib.world.StructureRegistry;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;

public class ModStructureRegistry {
    private final InfinityMod<?,?> mod;
    private final DeferredRegister<StructureProcessorList> register;

    protected ModStructureRegistry(InfinityMod<?,?> mod) {
        this.mod = mod;
        this.register = DeferredRegister.create(BuiltinRegistries.PROCESSOR_LIST.key(), mod.getModId());
    }

    protected final RegistryObject<StructureProcessorList> processor(String name, StructureProcessor... processors){
        return this.processor(name, ImmutableList.copyOf(processors));
    }

    protected final RegistryObject<StructureProcessorList> processor(String name, List<StructureProcessor> processors){
        return this.register.register(name, () -> new StructureProcessorList(processors));
    }

    protected final IInfStructure structure(String name, ResourceLocation targetPool, int weight, Holder<StructureProcessorList> processors, StructureTemplatePool.Projection placement) {
        return this.structure(name, ImmutableSet.of(targetPool), weight, processors, placement);
    }

    protected final IInfStructure structure(String name, Set<ResourceLocation> targetPools, int weight, Holder<StructureProcessorList> processors, StructureTemplatePool.Projection placement) {
       return this.structure(name, targetPools, weight, Either.left(processors), placement);
    }

    protected final IInfStructure structure(String name, ResourceLocation targetPool, int weight, RegistryObject<StructureProcessorList> processors, StructureTemplatePool.Projection placement) {
        return this.structure(name, ImmutableSet.of(targetPool), weight, processors, placement);
    }

    protected final IInfStructure structure(String name, Set<ResourceLocation> targetPools, int weight, RegistryObject<StructureProcessorList> processors, StructureTemplatePool.Projection placement) {
        return this.structure(name, targetPools, weight, Either.right(processors), placement);
    }

    private IInfStructure structure(String name, Set<ResourceLocation> targetPools, int weight, Either<Holder<StructureProcessorList>, RegistryObject<StructureProcessorList>> processors, StructureTemplatePool.Projection placement) {
        final ResourceLocation id = new ResourceLocation(this.mod.getModId(), name);
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
        StructureRegistry.getInstance().registerStructure(this.mod, structure);
        return structure;
    }
}
