package com.infinityraider.infinitylib.entity;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.modules.entitytargeting.ModuleEntityTargeting;
import com.infinityraider.infinitylib.utility.IToggleable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityRegistryEntry<E extends Entity> implements IToggleable {
    private static int lastId = 0;

    private Class<? extends E> entityClass;
    private String name;
    private boolean enabled;

    /** general entity data */
    private int trackingDistance;
    private int updateFrequency;
    private boolean velocityUpdates;

    /** spawn egg */
    private boolean hasEgg;
    private int primaryColor;
    private int secondaryColor;

    /** creature spawn */
    private boolean doSpawn;
    private int min;
    private int max;
    private int weight;
    private EnumCreatureType type;
    private Biome[] biomes;

    /** rendering */
    private IRenderFactory<E> renderFactory;

    public EntityRegistryEntry(Class<? extends  E> entityClass, String name) {
        this.entityClass = entityClass;
        this.name = name;
        this.trackingDistance = 32;
        this.updateFrequency = 1;
        this.velocityUpdates = true;
        this.enabled = true;
        this.hasEgg = false;
        this.doSpawn = false;
    }

    public EntityRegistryEntry<E> setTrackingDistance(int trackingDistance) {
        this.trackingDistance = trackingDistance;
        return this;
    }

    public EntityRegistryEntry<E> setUpdateFrequency(int updateFrequency) {
        this.updateFrequency = updateFrequency;
        return this;
    }

    public EntityRegistryEntry<E> setVelocityUpdates(boolean velocityUpdates) {
        this.velocityUpdates = velocityUpdates;
        return this;
    }

    public EntityRegistryEntry<E> setSpawnEgg(int primaryColor, int secondaryColor) {
        this.hasEgg = true;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        return this;
    }

    public EntityRegistryEntry<E> setSpawnEgg(int r1, int g1, int b1, int r2, int g2, int b2) {
        return this.setSpawnEgg( (r1 << 16) | (g1 << 8) | (b1), (r2 << 16) | (g2 << 8) | (b2) );
    }

    public EntityRegistryEntry<E> setCreatureSpawn(int min, int max, int weight, EnumCreatureType type, Biome[] biomes) {
        this.doSpawn = true;
        this.min = min;
        this.max = max;
        this.weight = weight;
        this.type = type;
        this.biomes = biomes;
        return this;
    }

    public EntityRegistryEntry<E> setRenderFactory(IRenderFactory<E> renderFactory) {
        this.renderFactory = renderFactory;
        return this;
    }

    public EntityRegistryEntry<E> setEntityTargetedBy(Class<? extends EntityCreature>... aggressors) {
        ModuleEntityTargeting module = ModuleEntityTargeting.getInstance();
        module.activate();
        for(Class<? extends EntityCreature> aggressor : aggressors) {
            module.registerEntityTargeting(this.entityClass, aggressor);
        }
        return this;
    }

    public void register(InfinityMod mod) {
        EntityRegistry.registerModEntity(entityClass, name, lastId, mod, trackingDistance, updateFrequency, velocityUpdates);
        if(hasEgg) {
            EntityRegistry.registerEgg(entityClass, primaryColor, secondaryColor);
        }
        if(doSpawn) {
            EntityRegistry.addSpawn(mod.getModId() + "." + name, weight, min, max, type, biomes);
        }
        lastId = lastId + 1;
    }

    @SideOnly(Side.CLIENT)
    public void registerClient(InfinityMod mod) {
        this.register(mod);
        RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
