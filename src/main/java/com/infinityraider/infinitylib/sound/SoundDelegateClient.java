package com.infinityraider.infinitylib.sound;

import com.infinityraider.infinitylib.InfinityLib;
import com.mojang.math.Vector3d;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class SoundDelegateClient extends SidedSoundDelegate implements SoundEventListener {
    private final SoundManager handler;
    private final Map<String, IModSound> soundMap;
    private final Set<String> cleanupPool;

    public SoundDelegateClient(SoundManager handler) {
        this.handler = handler;
        this.soundMap = new HashMap<>();
        this.cleanupPool = new HashSet<>();
        this.handler.addListener(this);
        InfinityLib.instance.proxy().registerEventHandler(this);
    }

    @Override
    public void onPlaySound(SoundInstance sound, WeighedSoundEvents accessor) { }

    @Override
    public SoundTask playSoundAtPositionOnce(Vector3d position, SoundEvent sound, SoundSource category, float volume, float pitch) {
        SoundTaskClient soundTask = new SoundTaskClient(Mth.createInsecureUUID(ThreadLocalRandom.current()).toString(), sound, category, volume, pitch).setRepeat(false);
        ModSoundAtPosition soundImpl = new ModSoundAtPosition(this, position, soundTask);
        this.handleSoundPlay(soundTask, soundImpl);
        return soundTask;
    }

    @Override
    public SoundTaskClient playSoundAtEntityOnce(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch) {
        SoundTaskClient soundTask = new SoundTaskClient(Mth.createInsecureUUID(ThreadLocalRandom.current()).toString(), sound, category, volume, pitch).setRepeat(false);
        ModSoundAtEntity soundImpl = new ModSoundAtEntity(this, entity, soundTask);
        this.handleSoundPlay(soundTask, soundImpl);
        return soundTask;
    }

    @Override
    public SoundTask playSoundAtPositionContinuous(Vector3d position, SoundEvent sound, SoundSource category, float volume, float pitch) {
        SoundTaskClient soundTask = new SoundTaskClient(Mth.createInsecureUUID(ThreadLocalRandom.current()).toString(), sound, category, volume, pitch).setRepeat(true).setRepeatDelay(0);
        ModSoundAtPosition soundImpl = new ModSoundAtPosition(this, position, soundTask);
        this.handleSoundPlay(soundTask, soundImpl);
        return soundTask;
    }

    @Override
    public SoundTaskClient playSoundAtEntityContinuous(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch) {
        SoundTaskClient soundTask = new SoundTaskClient(Mth.createInsecureUUID(ThreadLocalRandom.current()).toString(), sound, category, volume, pitch).setRepeat(true).setRepeatDelay(0);
        ModSoundAtEntity soundImpl = new ModSoundAtEntity(this, entity, soundTask);
        this.handleSoundPlay(soundTask, soundImpl);
        return soundTask;
    }

    protected void handleSoundPlay(SoundTaskClient soundTask, IModSound sound) {
        this.soundMap.put(soundTask.getUUID(), sound);
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    @Override
    public void stopSound(SoundTask task) {
        this.stopSound(task.getUUID());
    }

    @Override
    void onSoundMessage(MessagePlaySound message) {
        SoundTaskClient soundTask = message.getModSound();
        switch (message.getType()) {
            case ENTITY:
                this.handleSoundPlay(soundTask, new ModSoundAtEntity(this, message.getEntity(), soundTask));
                break;
            case POSITION:
                this.handleSoundPlay(soundTask, new ModSoundAtPosition(this, message.getPosition(), soundTask));
                break;
        }
    }

    @Override
    void onSoundMessage(MessageStopSound message) {
        this.stopSound(message.getUUID());
    }

    protected void stopSound(String UUID) {
        if(this.soundMap.containsKey(UUID)) {
            this.soundMap.remove(UUID).stopPlaying();
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.START) {
            this.cleanupPool.addAll(this.soundMap.keySet());
        } else {
            this.cleanupPool.forEach(this.soundMap::remove);
            this.cleanupPool.clear();
        }
    }

    void onSoundTick(IModSound sound) {
        this.cleanupPool.remove(sound.getUUID());
    }
}