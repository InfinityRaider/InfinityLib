package com.infinityraider.infinitylib.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;

public abstract class EntityLivingBase extends LivingEntity implements IBaseEntityImplementation<EntityLivingBase> {
    protected EntityLivingBase(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
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
