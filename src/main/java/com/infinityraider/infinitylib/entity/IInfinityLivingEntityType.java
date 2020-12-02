package com.infinityraider.infinitylib.entity;

import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.item.ItemGroup;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.util.Optional;

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
     * Retrieves the rules for ambient spawning of this entity in a given biome
     *
     * @param biome the biome
     * @return optional with the spawn rules or an empty optional if the entity should not spawn naturally in this biome
     */
    default Optional<SpawnRules> getSpawnRules(Biome biome) {
        return Optional.empty();
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

    interface SpawnRules {

        int min();

        int max();

        int weight();
    }
}
