package com.infinityraider.infinitylib.sound;

import com.infinityraider.infinitylib.utility.IStoppable;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IModSound extends SoundInstance, IStoppable {
    boolean repeat();

    int repeatDelay();

    IModSound setVolume(float volume);

    IModSound setPitch(float pitch);

    IModSound setRepeat(boolean repeat);

    IModSound setRepeatDelay(int ticks);

    String getUUID();
}