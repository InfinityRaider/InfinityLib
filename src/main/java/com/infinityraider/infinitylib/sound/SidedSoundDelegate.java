package com.infinityraider.infinitylib.sound;


import com.mojang.math.Vector3d;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public abstract class SidedSoundDelegate {
    public abstract SoundTask playSoundAtPositionOnce(Vector3d position, SoundEvent sound, SoundSource category, float volume, float pitch);

    public abstract SoundTask playSoundAtEntityOnce(Entity e, SoundEvent sound, SoundSource category, float volume, float pitch);

    public abstract SoundTask playSoundAtPositionContinuous(Vector3d position, SoundEvent sound, SoundSource category, float volume, float pitch);

    public abstract SoundTask playSoundAtEntityContinuous(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch);

    public abstract void stopSound(SoundTask task);

    abstract void onSoundMessage(MessagePlaySound message);

    abstract void onSoundMessage(MessageStopSound message);
}