package com.infinityraider.infinitylib.render.entity;

import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class RenderEntityAsBlock<T extends Entity> extends EntityRenderer<T> implements IRenderUtilities {
    protected RenderEntityAsBlock(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(T entity, float yaw, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light) {
        transforms.pushPose();
        this.applyTransformations(entity, yaw, partialTicks, transforms);
        this.renderBlockState(this.getBlockState(entity), transforms, buffer.getBuffer(this.getRenderType()));
        transforms.popPose();
    }

    protected abstract void applyTransformations(T entity, float yaw, float partialTicks, PoseStack transforms);

    protected abstract BlockState getBlockState(T entity);

    protected abstract RenderType getRenderType();

    @Override
    @ParametersAreNonnullByDefault
    public ResourceLocation getTextureLocation(T entity) {
        return this.getTextureAtlasLocation();
    }

    @Override
    public EntityRenderDispatcher getEntityRendererManager() {
        return this.entityRenderDispatcher;
    }
}
