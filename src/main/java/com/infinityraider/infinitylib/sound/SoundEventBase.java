package com.infinityraider.infinitylib.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nonnull;

public abstract class SoundEventBase extends SoundEvent implements IInfinitySoundEvent {
    private final String name;

    public SoundEventBase(String modId, String name) {
        super(new ResourceLocation(modId, name));
        this.name = name;
    }

    @Nonnull
    @Override
    public String getInternalName() {
        return this.name;
    }
}
