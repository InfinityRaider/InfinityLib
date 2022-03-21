package com.infinityraider.infinitylib.fluid;

import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nonnull;

public abstract class FluidBase extends Fluid implements IInfinityFluid {

    private final String internalName;

    public FluidBase(String name) {
        this.internalName = name;
    }

    public boolean isEnabled() {
        return true;
    }

    @Nonnull
    @Override
    public String getInternalName() {
        return internalName;
    }

}
