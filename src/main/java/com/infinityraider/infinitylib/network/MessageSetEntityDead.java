package com.infinityraider.infinitylib.network;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSetEntityDead extends MessageBase {
    private Entity entity;

    public MessageSetEntityDead() {
        super();
    }

    public MessageSetEntityDead(Entity entity) {
        this();
        this.entity = entity;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.entity != null && this.entity.isAlive()) {
            this.entity.remove(false);
        }
    }
}
