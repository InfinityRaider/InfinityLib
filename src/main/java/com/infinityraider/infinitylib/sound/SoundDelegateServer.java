package com.infinityraider.infinitylib.sound;


import com.mojang.math.Vector3d;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class SoundDelegateServer extends SidedSoundDelegate {
    public SoundDelegateServer() {
        super();
    }

    @Override
    public SoundTask playSoundAtPositionOnce(Vector3d position, SoundEvent sound, SoundSource category, float volume, float pitch) {
        SoundTask soundTask = new SoundTaskServer(sound, category, volume, pitch).setRepeat(false);
        new MessagePlaySound(position, (SoundTaskServer) soundTask).sendToAll();
        return soundTask;
    }

    @Override
    public SoundTask playSoundAtEntityOnce(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch) {
        SoundTask soundTask = new SoundTaskServer(sound, category, volume, pitch).setRepeat(false);
        new MessagePlaySound(entity, (SoundTaskServer) soundTask).sendToAll();
        return soundTask;
    }

    @Override
    public SoundTask playSoundAtPositionContinuous(Vector3d position, SoundEvent sound, SoundSource category, float volume, float pitch) {
        SoundTask soundTask = new SoundTaskServer(sound, category, volume, pitch).setRepeat(true).setRepeatDelay(0);
        new MessagePlaySound(position, (SoundTaskServer) soundTask).sendToAll();
        return soundTask;
    }

    @Override
    public SoundTask playSoundAtEntityContinuous(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch) {
        SoundTask soundTask = new SoundTaskServer(sound, category, volume, pitch).setRepeat(true).setRepeatDelay(0);
        new MessagePlaySound(entity, (SoundTaskServer) soundTask).sendToAll();
        return soundTask;
    }

    @Override
    public void stopSound(SoundTask task) {
        new MessageStopSound(task).sendToAll();
    }

    @Override
    void onSoundMessage(MessagePlaySound message) {
        //NO-OP
    }

    @Override
    void onSoundMessage(MessageStopSound soundTask) {
        //NO-OP
    }
}