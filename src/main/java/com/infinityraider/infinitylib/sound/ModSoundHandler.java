package com.infinityraider.infinitylib.sound;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class ModSoundHandler {
    private static final ModSoundHandler INSTANCE = new ModSoundHandler();

    public static final ModSoundHandler getInstance() {
        return INSTANCE;
    }

    private final SidedSoundDelegate delegate;

    private ModSoundHandler() {
        this.delegate = InfinityLib.proxy.getSoundDelegate();
    }

    public SoundTask playSoundAtEntityOnce(Entity entity, SoundEvent sound, SoundCategory category) {
        return this.playSoundAtEntityOnce(entity, sound, category, 1, 1);
    }

    public SoundTask playSoundAtEntityOnce(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        return this.delegate.playSoundAtEntityOnce(entity, sound, category, volume, pitch);
    }

    public SoundTask playSoundAtEntityContinuous(Entity entity, SoundEvent sound, SoundCategory category) {
        return this.playSoundAtEntityContinuous(entity, sound, category, 1, 1);
    }

    public SoundTask playSoundAtEntityContinuous(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        return this.delegate.playSoundAtEntityContinuous(entity, sound, category, volume, pitch);
    }

    public void stopSound(SoundTask sound) {
        sound.stop();
    }

    //to forward calls from the server
    void onSoundMessage(MessagePlaySound message) {
        this.delegate.onSoundMessage(message);
    }

    void onSoundMessage(MessageStopSound message) {
        this.delegate.onSoundMessage(message);
    }
}