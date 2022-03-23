package com.infinityraider.infinitylib.render.entity;

import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class RenderEntityAsItem<T extends Entity> extends EntityRenderer<T> implements IRenderUtilities {
    private final ItemStack item;

    public RenderEntityAsItem(EntityRendererProvider.Context renderManager, ItemStack item) {
        super(renderManager);
        this.item = item;
    }

    public ItemStack getItem() {
        return this.item;
    }

    private static final Quaternion ROTATION = Vector3f.YP.rotationDegrees(180);

    @Override
    @ParametersAreNonnullByDefault
    public void render(T entity, float yaw, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light) {
        transforms.pushPose();
        transforms.mulPose(this.getCameraOrientation());
        transforms.mulPose(ROTATION);
        this.applyTransformations(entity, yaw, partialTicks, transforms);
        this.renderItem(item, ItemTransforms.TransformType.GROUND, light, transforms, buffer);
        transforms.popPose();
    }

    protected abstract void applyTransformations(T entity, float yaw, float partialTicks, PoseStack transforms);

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
