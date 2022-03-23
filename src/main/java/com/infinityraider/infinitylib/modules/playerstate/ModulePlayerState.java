package com.infinityraider.infinitylib.modules.playerstate;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ModulePlayerState extends Module {
    private static final ModulePlayerState INSTANCE = new ModulePlayerState();
    private static final PlayerStateHandler STATE_HANDLER = PlayerStateHandler.getInstance();

    public static ModulePlayerState getInstance() {
        return INSTANCE;
    }

    private final ThreadLocal<HashMap<UUID, PlayerState>> states;

    private ModulePlayerState() {
        this.states = ThreadLocal.withInitial(HashMap::new);
    }

    PlayerState getState(Player player) {
        if(!states.get().containsKey(player.getUUID())) {
            states.get().put(player.getUUID(), PlayerState.createState(player));
        }
        return states.get().get(player.getUUID());
    }

    public void push(Player player, StatusEffect effect) {
        this.getState(player).push(effect);
    }

    public void pop(Player player, StatusEffect effect) {
        this.getState(player).pop(effect);
    }

    public void clear(Player player, StatusEffect effect) {
        this.getState(player).clear(effect);
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageSyncState.class);
    }

    @Override
    public List<Object> getCommonEventHandlers() {
        return ImmutableList.of(STATE_HANDLER);
    }

}
