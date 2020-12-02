package com.infinityraider.infinitylib.modules.playerstate;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSyncState extends MessageBase {
    private PlayerEntity player;
    private byte state;

    public MessageSyncState() {
        super();
    }

    public MessageSyncState(PlayerEntity player, State state) {
        this();
        this.player = player;
        this.state =
                (byte) ((state.isInvisible() ? 1 : 0)
                        | ((state.isInvulnerable() ? 1 : 0) << 1)
                        | ((state.isEthereal() ? 1 : 0) << 2)
                        | ((state.isUndetectable() ? 1 : 0) << 3));
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.player != null) {
            PlayerStateHandler.getInstance().getState(this.player)
                    .setInvisible(((this.state) & 1) == 1)
                    .setInvulnerable(((this.state >> 1) & 1) == 1)
                    .setEthereal(((this.state >> 2) & 1) == 1)
                    .setUndetectable(((this.state >> 3) & 1) == 1);
        }
    }
}
