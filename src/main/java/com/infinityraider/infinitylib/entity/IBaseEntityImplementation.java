package com.infinityraider.infinitylib.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public interface IBaseEntityImplementation<E extends Entity> extends IEntityAdditionalSpawnData {
    @SuppressWarnings("Unchecked")
    default E castToEntity() {
        return (E) this;
    }

    default IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this.castToEntity());
    }

    default void writeSpawnData(PacketBuffer buffer) {
        buffer.writeCompoundTag(this.castToEntity().writeWithoutTypeId(new CompoundNBT()));
    }

    default void readSpawnData(PacketBuffer buffer) {
        CompoundNBT tag = buffer.readCompoundTag();
        if(tag != null) {
            this.castToEntity().read(tag);
        }
    }

    void writeCustomEntityData(CompoundNBT tag);

    void readCustomEntityData(CompoundNBT tag);
}
