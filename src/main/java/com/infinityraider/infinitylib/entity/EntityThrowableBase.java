package com.infinityraider.infinitylib.entity;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public abstract class EntityThrowableBase extends ThrowableProjectile implements IBaseEntityImplementation<EntityThrowableBase> {
    protected EntityThrowableBase(EntityType<? extends ThrowableProjectile> type, Level world) {
        super(type, world);
    }

    protected EntityThrowableBase(EntityType<? extends ThrowableProjectile> type, Entity thrower) {
        this(type, thrower.getLevel());
        this.setPos(thrower.getX(), thrower.getEyeY() - (double)0.1F, thrower.getZ());
        this.setOwner(thrower);
    }

    @Nonnull
    @Override
    public final Packet<?> getAddEntityPacket() {
        return IBaseEntityImplementation.super.getAddEntityPacket();
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
