package com.infinityraider.infinitylib.modules.playerstate;

import net.minecraft.resources.ResourceLocation;

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

    public Update push() {
        boolean before = this.isActive();
        this.layers = this.layers + 1;
        boolean after = this.isActive();
        return this.checkUpdate(before, after);
    }

    public Update pop() {
        boolean before = this.isActive();
        this.layers = Math.max(0, this.layers - 1);
        boolean after = this.isActive();
        return this.checkUpdate(before, after);
    }

    public Update clear() {
        boolean before = this.isActive();
        this.layers = 0;
        boolean after = this.isActive();
        return this.checkUpdate(before, after);
    }

    public Update setPermanent(boolean permanent) {
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

    public enum Update {
        ACTIVATED,
        DEACTIVATED,
        NONE

    }

}
