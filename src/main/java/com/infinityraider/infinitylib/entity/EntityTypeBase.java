package com.infinityraider.infinitylib.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.network.FMLPlayMessages;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.*;

public class EntityTypeBase<T extends Entity> extends EntityType<T> implements IInfinityEntityType {
    private final String name;
    private final Class<T> entityClass;
    private final IRenderFactory<T> renderFactory;

    @SuppressWarnings("Unchecked")
    protected EntityTypeBase(String name, Class<T> entityClass,
                           EntityType.IFactory<T> factory, EntityClassification classification,
                           boolean p_i231489_3_, boolean summonable, boolean immuneToFire, boolean p_i231489_6_,
                           ImmutableSet<Block> blocks, EntitySize size, int trackingRange, int updateInterval, boolean velocityUpdates,
                           final BiFunction<FMLPlayMessages.SpawnEntity, World, T> customClientFactory,
                           Set<Class<? extends MobEntity>> aggressors, IRenderFactory<T> renderFactory) {

        super(factory, classification, p_i231489_3_, summonable, immuneToFire, p_i231489_6_, blocks, size, trackingRange, updateInterval,
                t -> velocityUpdates, t -> trackingRange, t -> updateInterval, customClientFactory);

        this.name = name;
        this.entityClass = entityClass;
        this.renderFactory = renderFactory;
        aggressors.forEach(this::setEntityTargetedBy);
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return this.entityClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IRenderFactory<T> getRenderFactory() {
        return this.renderFactory;
    }

    @Nonnull
    @Override
    public String getInternalName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static <T extends Entity> Builder<T> entityTypeBuilder(
            String name, Class<T> entityClass, EntityType.IFactory<T> clientFactory,
            EntityClassification classification, EntitySize size) {

        return new Builder<>(name, entityClass, clientFactory, classification, size);
    }

    public static <T extends Entity> Builder<T> entityTypeBuilder(
            String name, Class<T> entityClass,  BiFunction<FMLPlayMessages.SpawnEntity, World, T>  clientFactory,
            EntityClassification classification, EntitySize size) {

        return new Builder<>(name, entityClass, clientFactory, classification, size);
    }

    public static class Builder<T extends Entity> {
        protected final String name;
        protected final Class<T> entityClass;
        protected final EntityClassification classification;
        protected final EntitySize size;
        protected final Set<Block> blocks;
        protected final Set<Class<? extends MobEntity>> aggressors;

        protected EntityType.IFactory<T> factory;
        protected boolean p_i231489_3_;
        protected boolean summonable;
        protected boolean immuneToFire;
        protected boolean p_i231489_6_;
        protected int trackingRange;
        protected int updateInterval;
        protected boolean velocityUpdates;
        protected BiFunction<FMLPlayMessages.SpawnEntity, World, T> customClientFactory;
        protected IRenderFactory<T> renderFactory;

        private Builder(String name, Class<T> entityClass, EntityClassification classification, EntitySize size) {
            this.name = name;
            this.entityClass = entityClass;
            this.classification = classification;
            this.size = size;
            this.blocks = Sets.newIdentityHashSet();
            this.aggressors = Sets.newIdentityHashSet();
            this.trackingRange = 32;
            this.updateInterval = 1;
        }

        protected Builder(String name, Class<T> entityClass, EntityType.IFactory<T> factory,
                          EntityClassification classification, EntitySize size) {
            this(name, entityClass, classification, size);
            this.factory = factory;
        }

        protected Builder(String name, Class<T> entityClass, BiFunction<FMLPlayMessages.SpawnEntity, World, T> factory,
                          EntityClassification classification, EntitySize size) {
            this(name, entityClass, classification, size);
            this.customClientFactory = factory;
        }

        public EntityTypeBase<T> build() {
            return new EntityTypeBase<>(this.name, this.entityClass, this.factory, this.classification, this.p_i231489_3_,
                    this.summonable, this. immuneToFire, this.p_i231489_6_, ImmutableSet.copyOf(this.blocks), this.size,
                    this.trackingRange, this.updateInterval, this.velocityUpdates, this.customClientFactory,
                    this.aggressors, this.renderFactory);
        }

        public Builder<T> setCommonEntityFactory(EntityType.IFactory<T> factory) {
            this.factory = factory;
            return this;
        }

        public Builder<T> setClientEntityFactory(BiFunction<FMLPlayMessages.SpawnEntity, World, T> factory) {
            this.customClientFactory = factory;
            return this;
        }

        public Builder<T> setMysteryBoolean1(boolean value) {  //TODO: figure out what mystery boolean 1 does
            this.p_i231489_3_ = value;
            return this;
        }

        public Builder<T> setSummonable(boolean value) {
            this.summonable = value;
            return this;
        }

        public Builder<T> setImmuneToFire(boolean value) {
            this.immuneToFire = value;
            return this;
        }

        public Builder<T> setMysteryBoolean2(boolean value) {  //TODO: figure out what mystery boolean 1 does
            this.p_i231489_6_ = value;
            return this;
        }

        public Builder<T> addBlockForSomeReason(Block block) { //TODO: figure out what these blocks do
            this.blocks.add(block);
            return this;
        }

        public Builder<T> addBlocksForSomeReason(Collection<Block> blocks) { //TODO: figure out what these blocks do
            this.blocks.addAll(blocks);
            return this;
        }

        public Builder<T> addBlocksForSomeReason(Block... blocks) { //TODO: figure out what these blocks do
            this.blocks.addAll(Arrays.asList(blocks));
            return this;
        }

        public Builder<T> setTrackingRange(int value) {
            this.trackingRange = value;
            return this;
        }

        public Builder<T> setUpdateInterval(int value) {
            this.updateInterval = value;
            return this;
        }

        public Builder<T> setVelocityUpdates(boolean value) {
            this.velocityUpdates = value;
            return this;
        }

        public Builder<T> addAggressor(Class<? extends MobEntity> aggressor) {
            this.aggressors.add(aggressor);
            return this;
        }

        public Builder<T> addAggressors(Collection<Class<? extends MobEntity>> aggressors) {
            this.aggressors.addAll(aggressors);
            return this;
        }

        public Builder<T> addAggressors(Class<? extends MobEntity>... aggressors) {
            this.aggressors.addAll(Arrays.asList(aggressors));
            return this;
        }

        public Builder<T> setRenderFactory(IRenderFactory factory) {
            this.renderFactory = factory;
            return this;
        }
    }
}
