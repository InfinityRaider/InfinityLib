package com.infinityraider.infinitylib.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

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

        MobCategory classification();

        Predicate<Context> spawnRule();

        int min();

        int max();

        int weight();

        interface Context {
            Level world();

            BlockPos pos();

            BlockState stateBelow();

            Biome biome();
        }
    }
}
