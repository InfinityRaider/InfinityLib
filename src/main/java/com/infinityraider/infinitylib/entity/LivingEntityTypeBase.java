package com.infinityraider.infinitylib.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.network.FMLPlayMessages;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class LivingEntityTypeBase<T extends LivingEntity> extends EntityTypeBase<T> implements IInfinityLivingEntityType {
    private final Supplier<AttributeModifierMap> attributeSupplier;
    private final Optional<SpawnEggData> spawnEggData;
    private final Set<SpawnRule> spawnRules;

    protected LivingEntityTypeBase(String name, Class<T> entityClass,
                                   EntityType.IFactory<T> factory, EntityClassification classification,
                                   boolean p_i231489_3_, boolean summonable, boolean immuneToFire, boolean p_i231489_6_,
                                   ImmutableSet<Block> blocks, EntitySize size, int trackingRange, int updateInterval, boolean velocityUpdates,
                                   final BiFunction<FMLPlayMessages.SpawnEntity, World, T> customClientFactory,
                                   Set<Class<? extends MobEntity>> aggressors, IRenderFactory<T> renderFactory,
                                   Supplier<AttributeModifierMap> attributeSupplier, SpawnEggData spawnEggData,
                                   Set<SpawnRule> spawnRules) {

        super(name, entityClass, factory, classification, p_i231489_3_, summonable, immuneToFire, p_i231489_6_, blocks,
                size, trackingRange, updateInterval, velocityUpdates, customClientFactory, aggressors, renderFactory);
        this.attributeSupplier = attributeSupplier;
        this.spawnEggData = Optional.ofNullable(spawnEggData);
        this.spawnRules = spawnRules;
    }

    @Override
    public AttributeModifierMap createCustomAttributes() {
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
            String name, Class<T> entityClass, EntityClassification classification, EntitySize size) {
        return new Builder<>(name, entityClass, classification, size);
    }

    public static class Builder<T extends LivingEntity> extends EntityTypeBase.Builder<T> {
        private Supplier<AttributeModifierMap> attributeSupplier;
        private SpawnEggData spawnEggData;
        private Set<SpawnRule> spawnRules;

        protected Builder(String name, Class<T> entityClass, EntityClassification classification, EntitySize size) {
            super(name, entityClass, classification, size);
            this.spawnRules = Sets.newIdentityHashSet();
        }

        @Override
        public LivingEntityTypeBase<T> build() {
            return new LivingEntityTypeBase<>(this.name, this.entityClass, this.factory, this.classification, this.p_i231489_3_,
                    this.summonable, this. immuneToFire, this.p_i231489_6_, ImmutableSet.copyOf(this.blocks), this.size,
                    this.trackingRange, this.updateInterval, this.velocityUpdates, this.customClientFactory,
                    this.aggressors, this.renderFactory, this.attributeSupplier, this.spawnEggData, this.spawnRules);
        }

        public Builder<T> setAttributeSupplier(Supplier<AttributeModifierMap> attributeSupplier) {
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
