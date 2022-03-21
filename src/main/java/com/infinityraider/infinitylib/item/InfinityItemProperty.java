package com.infinityraider.infinitylib.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public abstract class InfinityItemProperty {
    private final ResourceLocation id;

    protected InfinityItemProperty(ResourceLocation id) {
        this.id = id;
    }

    public final ResourceLocation getId() {
        return this.id;
    }

    public abstract float getValue(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed);
}
