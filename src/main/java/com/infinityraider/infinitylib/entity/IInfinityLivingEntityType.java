package com.infinityraider.infinitylib.entity;

import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface IInfinityLivingEntityType extends IInfinityEntityType {
    /**
     * Defines the entity's attributes
     *
     * @return An attribute modifier map defining the entity's attributes
     */
    AttributeModifierMap createCustomAttributes();

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
    default EntityType<? extends LivingEntity> cast() {
        try {
            return (EntityType<? extends LivingEntity>) this;
        } catch(Exception e) {
            throw new ClassFormatException("IInfinityLivingEntityType must only be implemented in objects extending EntityType<? extends LivingEntity>");
        }
    }

    interface SpawnEggData {
        int primaryColor();

        int secondaryColor();

        @Nullable
        ItemGroup tab();
    }

    interface SpawnRule {

        EntityClassification classification();

        Predicate<Context> spawnRule();

        int min();

        int max();

        int weight();

        interface Context {
            IWorld world();

            BlockPos pos();

            BlockState stateBelow();

            Biome biome();
        }
    }
}
