package com.infinityraider.infinitylib.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CapabilityProvider<C> implements ICapabilitySerializable<CompoundNBT> {
    private final Capability<C> capability;
    private final C value;
    private final LazyOptional<C> valueHolder;

    public CapabilityProvider(Capability<C> capability, C value) {
        this.capability = capability;
        this.value = value;
        this.valueHolder = LazyOptional.of(this::getCapabilityValue);
    }

    public Capability<C> getCapability() {
        return this.capability;
    }

    @Nonnull
    public C getCapabilityValue() {
        return value;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) this.getCapability().getStorage().writeNBT(this.getCapability(), this.getCapabilityValue(), null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.getCapability().getStorage().readNBT(this.getCapability(), this.getCapabilityValue(), null, nbt);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == this.capability) {
            return valueHolder.cast();
        }
        return LazyOptional.empty();
    }
}
