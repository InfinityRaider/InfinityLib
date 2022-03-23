package com.infinityraider.infinitylib.modules.playerstate;

import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.IExtensibleEnum;

import java.util.Set;

public enum StatusEffect implements IExtensibleEnum {
    NONE(new ResourceLocation(InfinityLib.instance.getModId(), "none")),
    ETHEREAL(new ResourceLocation(InfinityLib.instance.getModId(), "ethereal")),
    INVISIBLE(new ResourceLocation(InfinityLib.instance.getModId(), "invisible")),
    INVULNERABLE(new ResourceLocation(InfinityLib.instance.getModId(), "invulnerable")),
    PHASED(new ResourceLocation(InfinityLib.instance.getModId(), "phased")),
    UNDETECTABLE(new ResourceLocation(InfinityLib.instance.getModId(), "undetectable"));

    private final ResourceLocation id;

    StatusEffect(ResourceLocation id) {
        this.id = id;
    }

    public final ResourceLocation getId() {
        return this.id;
    }

    public final boolean isValid() {
        return this != NONE;
    }

    public final boolean isActive(Player player) {
        return ModulePlayerState.getInstance().getState(player).isActive(this);
    }

    public void push(Player player) {
        ModulePlayerState.getInstance().push(player, this);
    }

    public void pop(Player player) {
        ModulePlayerState.getInstance().pop(player, this);
    }

    public void clear(Player player) {
        ModulePlayerState.getInstance().clear(player, this);
    }

    protected void onActivated(Player player) {
        listeners.forEach(listener -> listener.onActivated(player, this));
    }

    protected void onDeactivated(Player player) {
        listeners.forEach(listener -> listener.onDeactivated(player, this));
    }

    private static final Set<IListener> listeners = Sets.newIdentityHashSet();

    public static void addListener(IListener listener) {
        listeners.add(listener);
    }

    public static StatusEffect create(ResourceLocation id) {
        throw new IllegalStateException("Enum not extended");
    }

    public interface IListener {
        default void onActivated(Player player, StatusEffect effect) {}

        default void onDeactivated(Player player, StatusEffect effect) {}
    }
}
