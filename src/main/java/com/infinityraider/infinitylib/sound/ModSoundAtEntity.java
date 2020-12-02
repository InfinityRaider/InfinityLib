package com.infinityraider.infinitylib.sound;

import net.minecraft.entity.Entity;
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
            this.x = this.entity.getPosX();
            this.y = this.entity.getPosY();
            this.z = this.entity.getPosZ();
        } else {
            this.finishPlaying();
        }
    }
}