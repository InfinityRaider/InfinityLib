package com.infinityraider.infinitylib.sound;

import com.infinityraider.infinitylib.InfinityLib;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class SoundDelegateClient extends SidedSoundDelegate implements ISoundEventListener {
    private final SoundHandler handler;
    private final Map<String, IModSound> soundMap;
    private final Set<String> cleanupPool;

    public SoundDelegateClient(SoundHandler handler) {
        this.handler = handler;
        this.soundMap = new HashMap<>();
        this.cleanupPool = new HashSet<>();
        this.handler.addListener(this);
        InfinityLib.proxy.registerEventHandler(this);
    }

    @Override
    public void soundPlay(ISound soundIn, SoundEventAccessor accessor) {

    }

    @Override
    public SoundTaskClient playSoundAtEntityOnce(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        SoundTaskClient soundTask = new SoundTaskClient(MathHelper.getRandomUUID(ThreadLocalRandom.current()).toString(), sound, category, volume, pitch).setRepeat(false);
        ModSoundFollowEntity soundImpl = new ModSoundFollowEntity(this, entity, soundTask);
        this.handleSoundPlay(soundTask, soundImpl);
        return soundTask;
    }

    @Override
    public SoundTaskClient playSoundAtEntityContinuous(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        SoundTaskClient soundTask = new SoundTaskClient(MathHelper.getRandomUUID(ThreadLocalRandom.current()).toString(), sound, category, volume, pitch).setRepeat(true).setRepeatDelay(0);
        ModSoundFollowEntity soundImpl = new ModSoundFollowEntity(this, entity, soundTask);
        this.handleSoundPlay(soundTask, soundImpl);
        return soundTask;
    }

    protected void handleSoundPlay(SoundTaskClient soundTask, IModSound sound) {
        this.soundMap.put(soundTask.getUUID(), sound);
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }

    @Override
    public void stopSound(SoundTask task) {
        this.stopSound(task.getUUID());
    }

    @Override
    void onSoundMessage(MessagePlaySound message) {
        SoundTaskClient soundTask = message.getModSound();
        ModSoundFollowEntity soundImpl = new ModSoundFollowEntity(this, message.getEntity(), soundTask);
        this.handleSoundPlay(soundTask, soundImpl);
    }

    @Override
    void onSoundMessage(MessageStopSound message) {
        this.stopSound(message.getUUID());
    }

    protected void stopSound(String UUID) {
        if(this.soundMap.containsKey(UUID)) {
            this.soundMap.remove(UUID).stop();
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