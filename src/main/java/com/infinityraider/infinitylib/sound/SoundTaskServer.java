package com.infinityraider.infinitylib.sound;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class SoundTaskServer extends SoundTask {
    SoundTaskServer(SoundEvent sound, SoundSource category, float volume, float pitch) {
        super(Mth.createInsecureUUID(ThreadLocalRandom.current()).toString(), sound, category, volume, pitch);
    }
}