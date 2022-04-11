package com.infinityraider.infinitylib.sound;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageReader;
import com.infinityraider.infinitylib.network.serialization.IMessageWriter;
import com.infinityraider.infinitylib.network.serialization.PacketBufferUtil;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MessagePlaySound extends MessageBase {
    private Type type;
    private Entity entity;
    private Vec3 position;
    private String uuid;
    private SoundEvent sound;
    private SoundSource category;
    private float volume;
    private float pitch;
    private boolean repeat;
    private int repeatDelay;

    public MessagePlaySound() {
        super();
    }

    public MessagePlaySound(Entity entity, SoundTaskServer task) {
        this();
        this.type = Type.ENTITY;
        this.entity = entity;
        this.uuid = task.getUUID();
        this.sound = task.getSound();
        this.category = task.getCategory();
        this.volume = task.getVolume();
        this.pitch = task.getPitch();
        this.repeat = task.repeat();
        this.repeatDelay = task.repeatDelay();
    }

    public MessagePlaySound(Vec3 position, SoundTaskServer task) {
        this();
        this.type = Type.POSITION;
        this.position = position;
        this.uuid = task.getUUID();
        this.sound = task.getSound();
        this.category = task.getCategory();
        this.volume = task.getVolume();
        this.pitch = task.getPitch();
        this.repeat = task.repeat();
        this.repeatDelay = task.repeatDelay();
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.checkData() && this.sound != null && this.category != null) {
            ModSoundHandler.getInstance().onSoundMessage(this);
        }
    }

    protected boolean checkData() {
        if(this.type != null) {
            switch (this.type) {
                case ENTITY:
                    return this.entity != null;
                case POSITION:
                    return this.position != null;
            }
        }
        return false;
    }

    public Type getType() {
        return this.type;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public Vec3 getPosition() {
        return this.position;
    }

    @OnlyIn(Dist.CLIENT)
    public SoundTaskClient getModSound() {
        return new SoundTaskClient(this.uuid, this.sound, this.category, this.volume, this.pitch)
                .setVolume(this.volume)
                .setPitch(this.pitch)
                .setRepeat(this.repeat)
                .setRepeatDelay(this.repeatDelay);
    }

    @Override
    protected List<IMessageSerializer> getNecessarySerializers() {
        return ImmutableList.of(
                new IMessageSerializer<SoundEvent>() {
                    @Override
                    public boolean accepts(Class<SoundEvent> clazz) {
                        return clazz.isAssignableFrom(SoundEvent.class);
                    }

                    @Override
                    public IMessageWriter<SoundEvent> getWriter(Class<SoundEvent> clazz) {
                        return (buf, data) -> PacketBufferUtil.writeString(buf, data.getRegistryName().toString());
                    }

                    @Override
                    public IMessageReader<SoundEvent> getReader(Class<SoundEvent> clazz) {
                        return (data) -> (ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(PacketBufferUtil.readString(data))));
                    }
                });
    }

    public enum Type {
        ENTITY,
        POSITION
    }
}