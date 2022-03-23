package com.infinityraider.infinitylib.modules.playerstate;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.stream.Collectors;

public class MessageSyncState extends MessageBase {
    private Player player;
    private EnumMap<StatusEffect, Boolean> state;

    public MessageSyncState() {
        super();
    }

    public MessageSyncState(Player player, PlayerState state) {
        this();
        this.player = player;
        this.state = Arrays.stream(StatusEffect.values()).collect(Collectors.toMap(
                effect -> effect,
                state::isActive,
                (a, b) -> a == b ? a : true,
                () -> Maps.newEnumMap(StatusEffect.class)
        ));
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.player != null) {
            ModulePlayerState.getInstance().getState(this.player).onSync(this.state);
        }
    }
}
