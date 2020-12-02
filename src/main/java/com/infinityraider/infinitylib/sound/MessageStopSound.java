package com.infinityraider.infinitylib.sound;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageStopSound extends MessageBase {
    private String uuid;

    public MessageStopSound() {
        super();
    }

    public MessageStopSound(SoundTask task) {
        this();
        this.uuid = task.getUUID();
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.uuid != null) {
            ModSoundHandler.getInstance().onSoundMessage(this);
        }
    }

    public String getUUID() {
        return this.uuid;
    }
}