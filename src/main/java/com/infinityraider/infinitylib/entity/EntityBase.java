package com.infinityraider.infinitylib.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;

public abstract class EntityBase extends Entity implements IBaseEntityImplementation<EntityBase> {
    protected EntityBase(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public final IPacket<?> createSpawnPacket() {
        return IBaseEntityImplementation.super.createSpawnPacket();
    }

    @Override
    public final void writeAdditional(CompoundNBT tag) {
        this.writeCustomEntityData(tag);
    }

    @Override
    public final void readAdditional(CompoundNBT tag) {
        this.readCustomEntityData(tag);
    }
}
