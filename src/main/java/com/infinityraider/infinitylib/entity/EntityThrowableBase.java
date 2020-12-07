package com.infinityraider.infinitylib.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;

public abstract class EntityThrowableBase extends ThrowableEntity implements IBaseEntityImplementation<EntityThrowableBase> {
    protected EntityThrowableBase(EntityType<? extends ThrowableEntity> type, World world) {
        super(type, world);
    }

    protected EntityThrowableBase(EntityType<? extends ThrowableEntity> type, double x, double y, double z, World world) {
        super(type, x, y, z, world);
    }

    protected EntityThrowableBase(EntityType<? extends ThrowableEntity> type, LivingEntity thrower, World world) {
        super(type, thrower, world);
    }

    protected EntityThrowableBase(EntityType<? extends ThrowableEntity> type, LivingEntity thrower) {
        this(type,thrower, thrower.getEntityWorld());
    }

    @Override
    public final IPacket<?> createSpawnPacket() {
        return IBaseEntityImplementation.super.createSpawnPacket();
    }

    @Override
    public final void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        this.writeCustomEntityData(tag);
    }

    @Override
    public final void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        this.readCustomEntityData(tag);
    }
}
