package com.infinityraider.infinitylib.entity;

import com.infinityraider.infinitylib.InfinityMod;
import com.teaminfinity.elementalinvocations.utility.IToggleable;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityRegistryEntry<E extends Entity> implements IToggleable {
    private static int lastId = 0;

    private Class<? extends E> entityClass;
    private String name;

    private int trackingDistance;
    private int updateFrequency;
    private boolean velocityUpdates;
    private boolean enabled;
    private IRenderFactory<E> renderFactory;

    public EntityRegistryEntry(Class<? extends  E> entityClass, String name) {
        this.entityClass = entityClass;
        this.name = name;
        this.trackingDistance = 32;
        this.updateFrequency = 1;
        this.velocityUpdates = true;
        this.enabled = true;
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

    public EntityRegistryEntry<E> setRenderFactory(IRenderFactory<E> renderFactory) {
        this.renderFactory = renderFactory;
        return this;
    }

    public void register(InfinityMod mod) {
        EntityRegistry.registerModEntity(entityClass, name, lastId, mod, trackingDistance, updateFrequency, velocityUpdates);
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
