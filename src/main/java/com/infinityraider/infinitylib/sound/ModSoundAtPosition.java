package com.infinityraider.infinitylib.sound;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModSoundAtPosition extends ModSound {
    private final Vec3 position;

    protected ModSoundAtPosition(SoundDelegateClient delegate, Vec3 position, SoundTaskClient task) {

        super(delegate, task);
        this.position = position;
    }

    @Override
    protected void updateSound() {
        this.x = this.position.x;
        this.y = this.position.y;
        this.z = this.position.z;
    }
}