package com.infinityraider.infinitylib.entity;

import com.infinityraider.infinitylib.render.entity.RenderEntityEmpty;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class EmptyEntityRenderSupplier<T extends Entity> implements IEntityRenderSupplier<T> {
    private static final EmptyEntityRenderSupplier<?> INSTANCE = new EmptyEntityRenderSupplier<>();

    @SuppressWarnings("unchecked")
    public static <T extends Entity> IEntityRenderSupplier<T> getInstance() {
        return (EmptyEntityRenderSupplier<T>) INSTANCE;
    }

    private EmptyEntityRenderSupplier() {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public Supplier<EntityRendererProvider<T>> supplyRenderer() {
        return () -> RenderEntityEmpty::new;
    }
}
