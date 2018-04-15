package com.infinityraider.infinitylib.sound;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundDelegateServer extends SidedSoundDelegate {
    public SoundDelegateServer() {
        super();
    }

    @Override
    public SoundTask playSoundAtEntityOnce(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        SoundTask soundTask = new SoundTaskServer(sound, category, volume, pitch).setRepeat(false);
        new MessagePlaySound(entity, (SoundTaskServer) soundTask).sendToAll();
        return soundTask;
    }

    @Override
    public SoundTask playSoundAtEntityContinuous(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
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