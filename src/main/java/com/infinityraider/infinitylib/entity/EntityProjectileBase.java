package com.infinityraider.infinitylib.entity;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public abstract class EntityProjectileBase extends Projectile implements IBaseEntityImplementation<EntityProjectileBase> {
    protected EntityProjectileBase(EntityType<? extends Projectile> type, Level world) {
        super(type, world);
    }

    protected EntityProjectileBase(EntityType<? extends Projectile> type, Entity thrower) {
        this(type, thrower.getLevel());
        this.setOwner(thrower);
    }

    @Override
    public final Packet<?> createSpawnPacket() {
        return IBaseEntityImplementation.super.createSpawnPacket();
    }

    @Override
    public final void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.writeCustomEntityData(tag);
    }

    @Override
    public final void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readCustomEntityData(tag);
    }
}
