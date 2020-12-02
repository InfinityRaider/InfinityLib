package com.infinityraider.infinitylib.modules.dualwield;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSwingArm extends MessageBase {
    private PlayerEntity player;
    private int hand;

    public MessageSwingArm() {
        super();
    }

    public MessageSwingArm(PlayerEntity player, Hand hand) {
        this();
        this.player = player;
        this.hand = hand.ordinal();
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.player != null) {
            ArmSwingHandler.getInstance().swingArm(this.player, Hand.values()[this.hand]);
        }
    }
}
