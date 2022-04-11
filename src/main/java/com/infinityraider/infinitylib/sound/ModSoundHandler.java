package com.infinityraider.infinitylib.sound;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
public class ModSoundHandler {
    private static final ModSoundHandler INSTANCE = new ModSoundHandler();

    public static ModSoundHandler getInstance() {
        return INSTANCE;
    }

    private final SidedSoundDelegate delegate;

    private ModSoundHandler() {
        this.delegate = InfinityLib.instance.proxy().getSoundDelegate();
    }

    public SoundTask playSoundAtPositionOnce(double x, double y, double z, SoundEvent sound, SoundSource category) {
        return this.playSoundAtPositionOnce(new Vec3(x, y, z), sound, category);
    }

    public SoundTask playSoundAtPositionOnce(double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch) {
        return this.playSoundAtPositionOnce(new Vec3(x, y, z), sound, category, volume, pitch);
    }

    public SoundTask playSoundAtPositionOnce(Vec3 position, SoundEvent sound, SoundSource category) {
        return this.delegate.playSoundAtPositionOnce(position, sound, category, 1, 1);
    }

    public SoundTask playSoundAtPositionOnce(Vec3 position, SoundEvent sound, SoundSource category, float volume, float pitch) {
        return this.delegate.playSoundAtPositionOnce(position, sound, category, volume, pitch);
    }

    public SoundTask playSoundAtEntityOnce(Entity entity, SoundEvent sound, SoundSource category) {
        return this.playSoundAtEntityOnce(entity, sound, category, 1, 1);
    }

    public SoundTask playSoundAtEntityOnce(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch) {
        return this.delegate.playSoundAtEntityOnce(entity, sound, category, volume, pitch);
    }

    public SoundTask playSoundAtPositionContinuous(double x, double y, double z, SoundEvent sound, SoundSource category) {
        return this.playSoundAtPositionContinuous(new Vec3(x, y, z), sound, category);
    }

    public SoundTask playSoundAtPositionContinuous(double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch) {
        return this.playSoundAtPositionContinuous(new Vec3(x, y, z), sound, category, volume, pitch);
    }

    public SoundTask playSoundAtPositionContinuous(Vec3 position, SoundEvent sound, SoundSource category) {
        return this.delegate.playSoundAtPositionContinuous(position, sound, category, 1, 1);
    }

    public SoundTask playSoundAtPositionContinuous(Vec3 position, SoundEvent sound, SoundSource category, float volume, float pitch) {
        return this.delegate.playSoundAtPositionContinuous(position, sound, category, volume, pitch);
    }

    public SoundTask playSoundAtEntityContinuous(Entity entity, SoundEvent sound, SoundSource category) {
        return this.playSoundAtEntityContinuous(entity, sound, category, 1, 1);
    }

    public SoundTask playSoundAtEntityContinuous(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch) {
        return this.delegate.playSoundAtEntityContinuous(entity, sound, category, volume, pitch);
    }

    public void stopSound(SoundTask task) {
        this.delegate.stopSound(task);
    }

    //to forward calls from the server
    void onSoundMessage(MessagePlaySound message) {
        this.delegate.onSoundMessage(message);
    }

    void onSoundMessage(MessageStopSound message) {
        this.delegate.onSoundMessage(message);
    }
}