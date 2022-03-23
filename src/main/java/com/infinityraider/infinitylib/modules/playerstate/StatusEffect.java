package com.infinityraider.infinitylib.modules.playerstate;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.IExtensibleEnum;

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

    }

    public static StatusEffect create(ResourceLocation id) {
        throw new IllegalStateException("Enum not extended");
    }
}
