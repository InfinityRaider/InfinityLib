package com.infinityraider.infinitylib.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;

public class EntityTypeBase<T extends Entity> extends EntityType<T> implements IInfinityEntityType {
    private final String name;
    private final Class<T> entityClass;
    private final IEntityRenderSupplier<T> renderSupplier;

    @SuppressWarnings("Unchecked")
    protected EntityTypeBase(String name, Class<T> entityClass,
                             EntityType.EntityFactory<T> factory, MobCategory classification,
                             boolean serializable, boolean summonable, boolean immuneToFire, boolean p_i231489_6_,
                             ImmutableSet<Block> blocks, EntityDimensions size, int trackingRange, int updateInterval, boolean velocityUpdates,
                             final BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory,
                             Set<Class<? extends Mob>> aggressors, IEntityRenderSupplier<T> renderSupplier) {

        super(factory, classification, serializable, summonable, immuneToFire, p_i231489_6_, blocks, size, trackingRange, updateInterval,
                t -> velocityUpdates, t -> trackingRange, t -> updateInterval, customClientFactory);

        this.name = name;
        this.entityClass = entityClass;
        this.renderSupplier = renderSupplier;
        aggressors.forEach(this::setEntityTargetedBy);
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return this.entityClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IEntityRenderSupplier<T> getRenderSupplier() {
        return this.renderSupplier;
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
            String name, Class<T> entityClass, EntityType.EntityFactory<T> clientFactory,
            MobCategory classification, EntityDimensions size) {

        return new Builder<>(name, entityClass, clientFactory, classification, size);
    }

    public static <T extends Entity> Builder<T> entityTypeBuilder(
            String name, Class<T> entityClass,  BiFunction<PlayMessages.SpawnEntity, Level, T>  clientFactory,
            MobCategory classification, EntityDimensions size) {

        return new Builder<>(name, entityClass, clientFactory, classification, size);
    }

    public static class Builder<T extends Entity> {
        protected final String name;
        protected final Class<T> entityClass;
        protected final MobCategory classification;
        protected final EntityDimensions size;
        protected final Set<Block> blocks;
        protected final Set<Class<? extends Mob>> aggressors;

        protected EntityType.EntityFactory<T> factory;
        protected boolean serializable;
        protected boolean summonable;
        protected boolean immuneToFire;
        protected boolean mysteryBoolean;
        protected int trackingRange;
        protected int updateInterval;
        protected boolean velocityUpdates;
        protected BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory;
        protected IEntityRenderSupplier<T> renderFactory;

        private Builder(String name, Class<T> entityClass, MobCategory classification, EntityDimensions size) {
            this.name = name;
            this.entityClass = entityClass;
            this.classification = classification;
            this.size = size;
            this.blocks = Sets.newIdentityHashSet();
            this.aggressors = Sets.newIdentityHashSet();
            this.trackingRange = 32;
            this.updateInterval = 1;
        }

        protected Builder(String name, Class<T> entityClass, EntityType.EntityFactory<T> factory,
                          MobCategory classification, EntityDimensions size) {
            this(name, entityClass, classification, size);
            this.factory = factory;
        }

        protected Builder(String name, Class<T> entityClass, BiFunction<PlayMessages.SpawnEntity, Level, T> factory,
                          MobCategory classification, EntityDimensions size) {
            this(name, entityClass, classification, size);
            this.customClientFactory = factory;
        }

        public EntityTypeBase<T> build() {
            return new EntityTypeBase<>(this.name, this.entityClass, this.factory, this.classification, this.serializable,
                    this.summonable, this. immuneToFire, this.mysteryBoolean, ImmutableSet.copyOf(this.blocks), this.size,
                    this.trackingRange, this.updateInterval, this.velocityUpdates, this.customClientFactory,
                    this.aggressors, this.renderFactory);
        }

        public Builder<T> setCommonEntityFactory(EntityType.EntityFactory<T> factory) {
            this.factory = factory;
            return this;
        }

        public Builder<T> setClientEntityFactory(BiFunction<PlayMessages.SpawnEntity, Level, T> factory) {
            this.customClientFactory = factory;
            return this;
        }

        public Builder<T> setSerializable() {
            return this.setSerializable(true);
        }

        public Builder<T> setSerializable(boolean value) {
            this.serializable = value;
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

        public Builder<T> setMysteryBoolean(boolean value) {  //TODO: figure out what the mystery boolean does
            this.mysteryBoolean = value;
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

        public Builder<T> addAggressor(Class<? extends Mob> aggressor) {
            this.aggressors.add(aggressor);
            return this;
        }

        public Builder<T> addAggressors(Collection<Class<? extends Mob>> aggressors) {
            this.aggressors.addAll(aggressors);
            return this;
        }

        public Builder<T> addAggressors(Class<? extends Mob>... aggressors) {
            this.aggressors.addAll(Arrays.asList(aggressors));
            return this;
        }

        public Builder<T> setRenderFactory(IEntityRenderSupplier<T> factory) {
            this.renderFactory = factory;
            return this;
        }
    }
}
