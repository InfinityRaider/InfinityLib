package com.infinityraider.infinitylib.sound;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModSoundFollowEntity extends MovingSound implements IModSound {
    private final SoundDelegateClient delegate;
    private final Entity entity;
    private final String uuid;

    public ModSoundFollowEntity(SoundDelegateClient delegate, Entity entity, SoundTaskClient task) {
        super(task.getSound(), task.getCategory());
        this.delegate = delegate;
        this.entity = entity;
        this.uuid = task.getUUID();
        this.setVolume(task.getVolume());
        this.setPitch(task.getPitch());
        this.setRepeat(task.repeat());
        this.setRepeatDelay(task.repeatDelay());
    }

    @Override
    public boolean repeat() {
        return this.repeat;
    }

    @Override
    public int repeatDelay() {
        return this.repeatDelay;
    }

    @Override
    public IModSound setVolume(float volume) {
        this.volume = volume;
        return this;
    }

    @Override
    public IModSound setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    @Override
    public ModSoundFollowEntity setRepeat(boolean repeat) {
        this.repeat = repeat;
        return this;
    }

    @Override
    public ModSoundFollowEntity setRepeatDelay(int ticks) {
        this.repeatDelay = ticks;
        return this;
    }

    public void stop() {
        this.donePlaying = true;
    }

    @Override
    public void update() {
        if(this.entity.isEntityAlive()) {
            this.xPosF = (float) this.entity.posX;
            this.yPosF = (float) this.entity.posY;
            this.zPosF = (float) this.entity.posZ;
            this.delegate.onSoundTick(this);
        } else {
            this.donePlaying = true;
        }
    }

    @Override
    public String getUUID() {
        return this.uuid;
    }
}