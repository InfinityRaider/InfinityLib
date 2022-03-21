package com.infinityraider.infinitylib.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ModSound extends AbstractTickableSoundInstance implements IModSound {
    private final SoundDelegateClient delegate;
    private final String uuid;

    protected ModSound(SoundDelegateClient delegate, SoundTaskClient task) {
        super(task.getSound(), task.getCategory());
        this.delegate = delegate;
        this.uuid = task.getUUID();
        this.setVolume(task.getVolume());
        this.setPitch(task.getPitch());
        this.setRepeat(task.repeat());
        this.setRepeatDelay(task.repeatDelay());
    }

    @Override
    public final boolean repeat() {
        return this.looping;
    }

    @Override
    public final int repeatDelay() {
        return this.delay;
    }

    @Override
    public final ModSound setVolume(float volume) {
        this.volume = volume;
        return this;
    }

    @Override
    public final ModSound setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    @Override
    public final ModSound setRepeat(boolean repeat) {
        this.looping = repeat;
        return this;
    }

    @Override
    public final ModSound setRepeatDelay(int ticks) {
        this.delay = ticks;
        return this;
    }

    @Override
    public final String getUUID() {
        return this.uuid;
    }

    public final void stopPlaying() {
        this.stop();
    }

    @Override
    public final void tick() {
        this.delegate.onSoundTick(this);
        this.updateSound();
    }

    protected abstract void updateSound();
}