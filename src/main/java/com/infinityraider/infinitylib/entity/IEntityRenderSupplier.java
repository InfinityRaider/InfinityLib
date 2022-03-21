package com.infinityraider.infinitylib.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public interface IEntityRenderSupplier<E extends Entity> {
    @OnlyIn(Dist.CLIENT)
    Supplier<EntityRendererProvider<E>> supplyRenderer();
}
