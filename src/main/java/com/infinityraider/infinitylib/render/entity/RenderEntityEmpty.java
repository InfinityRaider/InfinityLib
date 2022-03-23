package com.infinityraider.infinitylib.render.entity;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class RenderEntityEmpty<T extends Entity> extends EntityRenderer<T> {

    public RenderEntityEmpty(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(T entity, float yaw, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light) {
        //NOOP
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull T entity) {
        return null;
    }
}
