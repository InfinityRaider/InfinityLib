package com.infinityraider.infinitylib.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.InfinityLib;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.ParametersAreNonnullByDefault;
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
        this.structures.stream()
                .filter(structure -> structure.weight() > 0)
                .forEach(structure -> rawTemplates.add(new Pair<>(getOrCreatePoolElement(structure), structure.weight())));

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

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class LegacyPoolElement extends LegacySinglePoolElement {
        protected LegacyPoolElement(IInfStructure structure) {
            super(Either.left(structure.id()), structure.processors(), structure.placement());
        }

        // overridden for breakpoints

        @Override
        protected StructurePlaceSettings getSettings(Rotation rotation, BoundingBox box, boolean p_210355_) {
            return super.getSettings(rotation, box, p_210355_);
        }

        @Override
        public StructurePoolElementType<?> getType() {
            return super.getType();
        }

        @Override
        public Vec3i getSize(StructureManager manager, Rotation rotation) {
            return super.getSize(manager, rotation);
        }

        @Override
        public List<StructureTemplate.StructureBlockInfo> getDataMarkers(StructureManager manager, BlockPos pos, Rotation rotation, boolean p_210461_) {
            return super.getDataMarkers(manager, pos, rotation, p_210461_);
        }

        @Override
        public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureManager manager, BlockPos pos, Rotation rotation, Random random) {
            return super.getShuffledJigsawBlocks(manager, pos, rotation, random);
        }

        @Override
        public BoundingBox getBoundingBox(StructureManager manager, BlockPos pos, Rotation rotation) {
            return super.getBoundingBox(manager, pos, rotation);
        }

        @Override
        public boolean place(StructureManager manager, WorldGenLevel level, StructureFeatureManager featureManager, ChunkGenerator generator, BlockPos min, BlockPos max, Rotation rotation, BoundingBox box, Random random, boolean p_210444_) {
            return super.place(manager, level, featureManager, generator, min, max, rotation, box, random, p_210444_);
        }

        @Override
        public void handleDataMarker(LevelAccessor world, StructureTemplate.StructureBlockInfo info, BlockPos pos, Rotation rotation, Random random, BoundingBox box) {
            super.handleDataMarker(world, info, pos, rotation, random, box);
        }

        @Override
        public StructurePoolElement setProjection(StructureTemplatePool.Projection projection) {
            return super.setProjection(projection);
        }

        @Override
        public StructureTemplatePool.Projection getProjection() {
            return super.getProjection();
        }

        @Override
        public int getGroundLevelDelta() {
            return super.getGroundLevelDelta();
        }
    }
}
