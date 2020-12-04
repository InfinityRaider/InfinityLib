package com.infinityraider.infinitylib.entity;

import com.infinityraider.infinitylib.render.entity.RenderEntityEmpty;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityRenderFactoryEmpty implements IRenderFactory<Entity> {
    private static final EntityRenderFactoryEmpty INSTANCE = new EntityRenderFactoryEmpty();

    @SuppressWarnings("unchecked")
    public static <T extends Entity> IRenderFactory<T> getInstance() {
        return (IRenderFactory<T>) INSTANCE;
    }

    private EntityRenderFactoryEmpty() {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public EntityRenderer<? super Entity> createRenderFor(EntityRendererManager manager) {
        return new RenderEntityEmpty(manager);
    }
}
