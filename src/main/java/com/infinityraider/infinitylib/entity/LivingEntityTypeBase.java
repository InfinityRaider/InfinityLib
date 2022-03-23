package com.infinityraider.infinitylib.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.PlayMessages;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class LivingEntityTypeBase<T extends LivingEntity> extends EntityTypeBase<T> implements IMobEntityType {
    private final Supplier<AttributeSupplier> attributeSupplier;
    private final Optional<SpawnEggData> spawnEggData;
    private final Set<SpawnRule> spawnRules;

    protected LivingEntityTypeBase(String name, Class<T> entityClass,
                                   EntityType.EntityFactory<T> factory, MobCategory classification,
                                   boolean p_i231489_3_, boolean summonable, boolean immuneToFire, boolean p_i231489_6_,
                                   ImmutableSet<Block> blocks, EntityDimensions size, int trackingRange, int updateInterval, boolean velocityUpdates,
                                   final BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory,
                                   Set<Class<? extends Mob>> aggressors, IEntityRenderSupplier<T> renderFactory,
                                   Supplier<AttributeSupplier> attributeSupplier, SpawnEggData spawnEggData,
                                   Set<SpawnRule> spawnRules) {

        super(name, entityClass, factory, classification, p_i231489_3_, summonable, immuneToFire, p_i231489_6_, blocks,
                size, trackingRange, updateInterval, velocityUpdates, customClientFactory, aggressors, renderFactory);
        this.attributeSupplier = attributeSupplier;
        this.spawnEggData = Optional.ofNullable(spawnEggData);
        this.spawnRules = spawnRules;
    }

    @Override
    public AttributeSupplier createCustomAttributes() {
        return this.attributeSupplier.get();
    }

    @Override
    public Optional<SpawnEggData> getSpawnEggData() {
        return this.spawnEggData;
    }

    @Override
    public Set<SpawnRule> getSpawnRules() {
        return this.spawnRules;
    }

    public static <T extends LivingEntity> Builder<T> livingEntityTypeBuilder(
            String name, Class<T> entityClass, EntityType.EntityFactory<T> factory,
            MobCategory classification, EntityDimensions size) {

        return new Builder<>(name, entityClass, factory, classification, size);
    }

    public static <T extends LivingEntity> EntityTypeBase.Builder<T> livingEntityTypeBuilder(
            String name, Class<T> entityClass, BiFunction<PlayMessages.SpawnEntity, Level, T>  clientFactory,
            MobCategory classification, EntityDimensions size) {

        return new Builder<>(name, entityClass, clientFactory, classification, size);
    }

    public static class Builder<T extends LivingEntity> extends EntityTypeBase.Builder<T> {
        private Supplier<AttributeSupplier> attributeSupplier;
        private SpawnEggData spawnEggData;
        private final Set<SpawnRule> spawnRules;

        protected Builder(String name, Class<T> entityClass, EntityType.EntityFactory<T> factory,
                          MobCategory classification, EntityDimensions size) {
            super(name, entityClass, factory, classification, size);
            this.spawnRules = Sets.newIdentityHashSet();
        }

        protected Builder(String name, Class<T> entityClass,  BiFunction<PlayMessages.SpawnEntity, Level, T> factory,
                          MobCategory classification, EntityDimensions size) {
            super(name, entityClass, factory, classification, size);
            this.spawnRules = Sets.newIdentityHashSet();
        }

        @Override
        public LivingEntityTypeBase<T> build() {
            return new LivingEntityTypeBase<>(this.name, this.entityClass, this.factory, this.classification, this.serializable,
                    this.summonable, this. immuneToFire, this.mysteryBoolean, ImmutableSet.copyOf(this.blocks), this.size,
                    this.trackingRange, this.updateInterval, this.velocityUpdates, this.customClientFactory,
                    this.aggressors, this.renderFactory, this.attributeSupplier, this.spawnEggData, this.spawnRules);
        }

        public Builder<T> setAttributeSupplier(Supplier<AttributeSupplier> attributeSupplier) {
            this.attributeSupplier = attributeSupplier;
            return this;
        }

        public Builder<T> setSpawnEggData(SpawnEggData spawnEggData) {
            this.spawnEggData = spawnEggData;
            return this;
        }

        public Builder<T> addSpawnRule(SpawnRule spawnRule) {
            this.spawnRules.add(spawnRule);
            return this;
        }

        public Builder<T> addSpawnRules(Collection<SpawnRule> spawnRules) {
            this.spawnRules.addAll(spawnRules);
            return this;
        }

        public Builder<T> addSpawnRules(SpawnRule... spawnRules) {
            this.spawnRules.addAll(Arrays.asList(spawnRules));
            return this;
        }

    }
}
