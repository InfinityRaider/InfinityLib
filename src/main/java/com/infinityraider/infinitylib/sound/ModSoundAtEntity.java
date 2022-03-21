package com.infinityraider.infinitylib.sound;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModSoundAtEntity extends ModSound {
    private final Entity entity;

    public ModSoundAtEntity(SoundDelegateClient delegate, Entity entity, SoundTaskClient task) {
        super(delegate, task);
        this.entity = entity;
    }

    @Override
    public void updateSound() {
        if(this.entity.isAlive()) {
            this.x = this.entity.getX();
            this.y = this.entity.getY();
            this.z = this.entity.getZ();
        } else {
            this.stopPlaying();
        }
    }
}