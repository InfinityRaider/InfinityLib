package com.infinityraider.infinitylib.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public abstract class EntityLivingBase extends LivingEntity implements IBaseEntityImplementation<EntityLivingBase> {
    protected EntityLivingBase(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public final Packet<?> getAddEntityPacket() {
        return IBaseEntityImplementation.super.getAddEntityPacket();
    }

    @Override
    public final void addAdditionalSaveData(@Nonnull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.writeCustomEntityData(tag);
    }

    @Override
    public final void readAdditionalSaveData(@Nonnull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readCustomEntityData(tag);
    }
}
