package com.infinityraider.infinitylib.entity;

import com.infinityraider.infinitylib.render.entity.RenderEntityEmpty;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class EmptyEntityRenderSupplier implements IEntityRenderSupplier<Entity> {
    private static final EmptyEntityRenderSupplier INSTANCE = new EmptyEntityRenderSupplier();

    @SuppressWarnings("unchecked")
    public static IEntityRenderSupplier<Entity> getInstance() {
        return INSTANCE;
    }

    private EmptyEntityRenderSupplier() {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public Supplier<EntityRendererProvider<Entity>> supplyRenderer() {
        return () -> RenderEntityEmpty::new;
    }
}
