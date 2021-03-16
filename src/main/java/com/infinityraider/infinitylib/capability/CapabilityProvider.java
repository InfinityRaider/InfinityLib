package com.infinityraider.infinitylib.capability;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public final class CapabilityProvider<C> implements ICapabilitySerializable<CompoundNBT> {
    private final Supplier<Capability<C>> capabilitySupplier;
    private final C value;
    private final LazyOptional<C> valueHolder;

    @Deprecated
    public CapabilityProvider(Capability<C> capability, C value) {
        this(() -> capability, value);
    }

    public CapabilityProvider(Supplier<Capability<C>> capability, C value) {
        this.capabilitySupplier = capability;
        this.value = value;
        this.valueHolder = LazyOptional.of(this::getCapabilityValue);
    }

    public Capability<C> getCapability() {
        return this.capabilitySupplier.get();
    }

    @Nonnull
    public C getCapabilityValue() {
        return value;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return this.checkCapability()
                .map(capability -> (CompoundNBT) capability.getStorage().writeNBT(capability, this.getCapabilityValue(), null))
                .orElse(new CompoundNBT());
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.checkCapability().ifPresent(capability -> capability.getStorage().readNBT(capability, this.getCapabilityValue(), null, nbt));
    }

    protected Optional<Capability<C>> checkCapability() {
        Capability<C> capability = this.getCapability();
        if(capability == null) {
            InfinityLib.instance.getLogger().error(
                    "[SEVERE] Capability implementation is requested before injection, report this to the mod author");
            InfinityLib.instance.getLogger().printStackTrace(
                    new RuntimeException("[SEVERE] Encountered null capability for: " + this.getCapabilityValue().getClass().getName()));
        }
        return Optional.ofNullable(capability);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == this.getCapability()) {
            return valueHolder.cast();
        }
        return LazyOptional.empty();
    }
}
