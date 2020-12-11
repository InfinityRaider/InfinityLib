package com.infinityraider.infinitylib.item.property;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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

    public abstract float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity);
}
