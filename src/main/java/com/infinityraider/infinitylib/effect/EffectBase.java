package com.infinityraider.infinitylib.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import javax.annotation.Nonnull;

public class EffectBase extends Effect implements IInfinityEffect {
    private final String name;

    protected EffectBase(String name, EffectType type, int liquidColor) {
        super(type, liquidColor);
        this.name = "effect." + name;
    }

    @Nonnull
    @Override
    public String getInternalName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
