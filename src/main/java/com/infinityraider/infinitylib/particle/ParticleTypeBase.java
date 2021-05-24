package com.infinityraider.infinitylib.particle;

import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

import javax.annotation.Nonnull;

public abstract class ParticleTypeBase<D extends IParticleData> extends ParticleType<D> implements IInfinityParticleType<D> {
    private final String internalName;

    @SuppressWarnings("deprecation")
    public ParticleTypeBase(String name, boolean alwaysShow) {
        super(alwaysShow, IInfinityParticleType.deserializer());
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
