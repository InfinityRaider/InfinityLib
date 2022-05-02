package com.infinityraider.infinitylib.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public interface IMobEntityType extends IInfinityEntityType {
    /**
     * Defines the entity's attributes
     *
     * @return An attribute modifier map defining the entity's attributes
     */
    AttributeSupplier createCustomAttributes();

    /**
     * Retrieves data for this entity's spawn egg
     *
     * @return optional with the spawn egg data, or an empty optional if no spawn egg is to be made
     */
    default Optional<SpawnEggData> getSpawnEggData() {
        return Optional.empty();
    }

    /**
     * Retrieves the rules for ambient spawning of this entity
     *
     * @return set with the spawn rules (can be empty)
     */
    default Set<SpawnRule> getSpawnRules() {
        return Collections.emptySet();
    }

    /**
     * Method to self cast to EntityType<? extends LivingEntity>
     * @return this, but typecast as EntityType<? extends LivingEntity>
     */
    @Override
    @SuppressWarnings("Unchecked")
    default EntityType<? extends Mob> cast() {
        try {
            return (EntityType<? extends Mob>) this;
        } catch(Exception e) {
            throw new IllegalArgumentException("IInfinityLivingEntityType must only be implemented in objects extending EntityType<? extends LivingEntity>");
        }
    }

    interface SpawnEggData {
        int primaryColor();

        int secondaryColor();

        @Nullable
        CreativeModeTab tab();
    }

    interface SpawnRule {
        boolean biomeCheck(@Nullable ResourceLocation biomeId, Biome.ClimateSettings climate, Biome.BiomeCategory category, BiomeSpecialEffects effects);

        MobCategory classification();

        SpawnPlacements.Type spawnType();

        Heightmap.Types heightType();

        boolean canSpawn(ServerLevelAccessor world, MobSpawnType spawnType, BlockPos pos, Random random);

        int min();

        int max();

        int weight();
    }
}
