package com.infinityraider.infinitylib.modules.dualwield;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageSwingArm extends MessageBase {
    private Player player;
    private int hand;

    public MessageSwingArm() {
        super();
    }

    public MessageSwingArm(Player player, InteractionHand hand) {
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
            ArmSwingHandler.getInstance().swingArm(this.player, InteractionHand.values()[this.hand]);
        }
    }
}
