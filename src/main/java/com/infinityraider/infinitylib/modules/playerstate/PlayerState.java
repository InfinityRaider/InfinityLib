package com.infinityraider.infinitylib.modules.playerstate;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;

import java.util.EnumMap;

public abstract class PlayerState {
    public static PlayerState createState(Player player) {
        return InfinityLib.instance.proxy().getLogicalSide().isClient() ? new Client(player) : new Server(player);
    }

    /** Player pointer */
    private final Player player;

    private PlayerState(Player player) {
        this.player = player;
    }

    public final Player getPlayer() {
        return this.player;
    }

    public abstract boolean isActive(StatusEffect status);

    public abstract void push(StatusEffect effect);

    public abstract void pop(StatusEffect effect);

    public abstract void clear(StatusEffect effect);

    protected abstract void onSync(EnumMap<StatusEffect, Boolean> map);

    private static class Server extends PlayerState {
        /** Statuses */
        private final EnumMap<StatusEffect, StatusTracker> statuses;

        Server(Player player) {
            super(player);
            this.statuses = Maps.newEnumMap(StatusEffect.class);
        }

        @Override
        public boolean isActive(StatusEffect status) {
            return this.getTracker(status).isActive();
        }

        @Override
        public void push(StatusEffect effect) {
            if(effect.isValid()) {
                StatusTracker.Update update = this.getTracker(effect).push();
                if(update.hasUpdated()) {
                    update.callBack(effect, this.getPlayer());
                    this.syncToClient();
                }
            }
        }

        @Override
        public void pop(StatusEffect effect) {
            if(effect.isValid()) {
                StatusTracker.Update update = this.getTracker(effect).pop();
                if(update.hasUpdated()) {
                    update.callBack(effect, this.getPlayer());
                    this.syncToClient();
                }
            }
        }

        @Override
        public void clear(StatusEffect effect) {
            if (effect.isValid()) {
                StatusTracker.Update update = this.getTracker(effect).clear();
                if(update.hasUpdated()) {
                    update.callBack(effect, this.getPlayer());
                    this.syncToClient();
                }
            }
        }

        @Override
        protected void onSync(EnumMap<StatusEffect, Boolean> map) {}

        protected StatusTracker getTracker(StatusEffect effect) {
            return this.statuses.computeIfAbsent(effect,StatusTracker::new);
        }

        private void syncToClient() {
            if(InfinityLib.instance.getEffectiveSide() == LogicalSide.SERVER) {
                new MessageSyncState(getPlayer(), this).sendToAll();
            }
        }
    }

    private static class Client extends PlayerState {
        private final EnumMap<StatusEffect, Boolean> status;

        private Client(Player player) {
            super(player);
            this.status = Maps.newEnumMap(StatusEffect.class);
        }

        @Override
        public boolean isActive(StatusEffect status) {
            return this.status.computeIfAbsent(status, s -> false);
        }

        @Override
        public void push(StatusEffect effect) {}

        @Override
        public void pop(StatusEffect effect) {}

        @Override
        public void clear(StatusEffect effect) {}

        @Override
        protected void onSync(EnumMap<StatusEffect, Boolean> map) {
            this.status.clear();
            this.status.putAll(map);
        }
    }
}
