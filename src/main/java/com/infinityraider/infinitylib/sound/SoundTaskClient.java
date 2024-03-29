package com.infinityraider.infinitylib.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundTaskClient extends SoundTask {
    SoundTaskClient(String uuid, SoundEvent sound, SoundSource category, float volume, float pitch) {
        super(uuid, sound, category, volume, pitch);
    }

    @Override
    public SoundTaskClient setVolume(float volume) {
        super.setVolume(volume);
        return this;
    }

    @Override
    public SoundTaskClient setPitch(float pitch) {
        super.setPitch(pitch);
        return this;
    }

    @Override
    public SoundTaskClient setRepeat(boolean repeat) {
        super.setRepeat(repeat);
        return this;
    }

    @Override
    public SoundTaskClient setRepeatDelay(int ticks) {
        super.setRepeatDelay(ticks);
        return this;
    }
}