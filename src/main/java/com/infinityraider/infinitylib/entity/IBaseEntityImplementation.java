package com.infinityraider.infinitylib.entity;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;

public interface IBaseEntityImplementation<E extends Entity> extends IEntityAdditionalSpawnData {
    @SuppressWarnings("Unchecked")
    default E castToEntity() {
        return (E) this;
    }

    @Nonnull
    default Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this.castToEntity());
    }

    default void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.castToEntity().saveWithoutId(new CompoundTag()));
    }

    default void readSpawnData(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        if(tag != null) {
            this.castToEntity().load(tag);
        }
    }

    void writeCustomEntityData(CompoundTag tag);

    void readCustomEntityData(CompoundTag tag);
}
