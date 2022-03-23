package com.infinityraider.infinitylib.modules.playerstate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class StatusTracker {
    private final StatusEffect handler;

    private int layers;
    private boolean permanent;

    public StatusTracker(StatusEffect handler) {
        this.handler = handler;
    }

    public final StatusEffect getHandler() {
        return this.handler;
    }

    public final ResourceLocation getId() {
        return this.getHandler().getId();
    }

    public boolean isActive() {
        return this.permanent || this.layers > 0;
    }

    protected Update push() {
        boolean before = this.isActive();
        this.layers = this.layers + 1;
        boolean after = this.isActive();
        return this.checkUpdate(before, after);
    }

    protected Update pop() {
        boolean before = this.isActive();
        this.layers = Math.max(0, this.layers - 1);
        boolean after = this.isActive();
        return this.checkUpdate(before, after);
    }

    protected Update clear() {
        boolean before = this.isActive();
        this.layers = 0;
        boolean after = this.isActive();
        return this.checkUpdate(before, after);
    }

    protected Update setPermanent(boolean permanent) {
        boolean before = this.isActive();
        this.permanent = permanent;
        boolean after = this.isActive();
        return this.checkUpdate(before, after);
    }

    private Update checkUpdate(boolean before, boolean after) {
        if(before != after) {
            if(before) {
                return Update.DEACTIVATED;
            } else {
                return Update.ACTIVATED;
            }
        }
        return Update.NONE;
    }

    protected enum Update {
        ACTIVATED(StatusEffect::onActivated),
        DEACTIVATED(StatusEffect::onDeactivated),
        NONE(((effect, player) -> {}));

        private final BiConsumer<StatusEffect, Player> callback;

        Update(BiConsumer<StatusEffect, Player> callback) {
            this.callback = callback;
        }

        protected void callBack(StatusEffect effect, Player player) {
            this.callback.accept(effect, player);
        }

        protected boolean hasUpdated() {
            return this != NONE;
        }

    }

}
