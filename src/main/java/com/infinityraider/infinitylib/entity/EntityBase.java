package com.infinityraider.infinitylib.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public abstract class EntityBase extends Entity implements IBaseEntityImplementation<EntityBase> {
    protected EntityBase(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Nonnull
    @Override
    public final Packet<?> getAddEntityPacket() {
        return IBaseEntityImplementation.super.getAddEntityPacket();
    }

    @Override
    public final void addAdditionalSaveData(CompoundTag tag) {
        this.writeCustomEntityData(tag);
    }

    @Override
    public final void readAdditionalSaveData(CompoundTag tag) {
        this.readCustomEntityData(tag);
    }
}
