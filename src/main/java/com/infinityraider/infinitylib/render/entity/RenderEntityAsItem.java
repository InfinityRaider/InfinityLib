package com.infinityraider.infinitylib.render.entity;

import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.inventory.container.PlayerContainer.LOCATION_BLOCKS_TEXTURE;

public abstract class RenderEntityAsItem<T extends Entity> extends EntityRenderer<T> implements IRenderUtilities {
    private final ItemStack item;

    public RenderEntityAsItem(EntityRendererManager renderManager, ItemStack item) {
        super(renderManager);
        this.item = item;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public ItemRenderer getItemRenderer() {
        return Minecraft.getInstance().getItemRenderer();
    }

    private static final Quaternion ROTATION = Vector3f.YP.rotationDegrees(180);

    @Override
    @ParametersAreNonnullByDefault
    public void render(T entity, float yaw, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light) {
        transforms.push();

        transforms.rotate(this.getCameraOrientation());
        transforms.rotate(ROTATION);
        this.applyTransformations(entity, yaw, partialTicks, transforms);
        this.getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, transforms, buffer);

        transforms.pop();
    }

    public abstract void applyTransformations(T entity, float yaw, float partialTicks, MatrixStack transforms);

    @Override
    @ParametersAreNonnullByDefault
    public ResourceLocation getEntityTexture(T entity) {
        return LOCATION_BLOCKS_TEXTURE;
    }

    @Override
    public EntityRendererManager getEntityRendererManager() {
        return this.renderManager;
    }
}
