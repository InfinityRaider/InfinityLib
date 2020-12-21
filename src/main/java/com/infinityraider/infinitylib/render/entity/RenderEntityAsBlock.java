package com.infinityraider.infinitylib.render.entity;

import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class RenderEntityAsBlock<T extends Entity> extends EntityRenderer<T> implements IRenderUtilities {
    protected RenderEntityAsBlock(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(T entity, float yaw, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light) {
        transforms.push();
        this.applyTransformations(entity, yaw, partialTicks, transforms);
        this.renderBlockState(this.getBlockState(entity), transforms, buffer.getBuffer(this.getRenderType()));
        transforms.pop();
    }

    protected abstract void applyTransformations(T entity, float yaw, float partialTicks, MatrixStack transforms);

    protected abstract BlockState getBlockState(T entity);

    protected abstract RenderType getRenderType();

    @Override
    @ParametersAreNonnullByDefault
    public ResourceLocation getEntityTexture(T entity) {
        return this.getTextureAtlasLocation();
    }

    @Override
    public EntityRendererManager getEntityRendererManager() {
        return this.renderManager;
    }
}
