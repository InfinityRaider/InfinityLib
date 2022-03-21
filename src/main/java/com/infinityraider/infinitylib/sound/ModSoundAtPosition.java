package com.infinityraider.infinitylib.sound;

import com.mojang.math.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModSoundAtPosition extends ModSound {
    private final Vector3d position;

    protected ModSoundAtPosition(SoundDelegateClient delegate, Vector3d position, SoundTaskClient task) {
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