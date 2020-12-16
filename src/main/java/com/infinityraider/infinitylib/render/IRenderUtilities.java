package com.infinityraider.infinitylib.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Interface with utility methods which can make life easier when rendering stuff.
 * Implement in any render class to gain access to these methods.
 */
@OnlyIn(Dist.CLIENT)
public interface IRenderUtilities {

    /**
     * Renders an item
     */
    default void renderItem(ItemStack stack, ItemCameraTransforms.TransformType perspective, int light, int overlay,
                            MatrixStack transforms, IRenderTypeBuffer buffer) {
        this.getItemRenderer().renderItem(stack, perspective, light, overlay, transforms, buffer);
    }

    /**
     * @return the TextureAtlasSprite for the missing texture
     */
    default TextureAtlasSprite getMissingSprite() {
        if (Objects.missingSprite == null) {
            Objects.missingSprite = this.getSprite(MissingTextureSprite.getLocation());
        }
        return Objects.missingSprite;
    }

    /**
     * Fetches the sprite on the Texture Atlas related to a Resource Location
     * @param location the Resource Location
     * @return the sprite
     */
    default TextureAtlasSprite getSprite(ResourceLocation location) {
        return this.getTextureAtlas().getSprite(location);
    }

    /**
     * Binds a texture for rendering
     * @param location the ResourceLocation for the texture
     */
    default void bindTexture(ResourceLocation location) {
        this.getTextureManager().bindTexture(location);
    }

    /**
     * Binds the texture atlas for rendering
     */
    default void bindTextureAtlas() {
        this.bindTexture(this.getTextureAtlasLocation());
    }

    /**
     * Fetches the AtlasTexture object representing the Texture Atlas
     * @return the AtlasTexture object
     */
    default AtlasTexture getTextureAtlas() {
        return this.getModelManager().getAtlasTexture(this.getTextureAtlasLocation());
    }

    /**
     * Fetches the ResourceLocation associated with the Texture Atlas
     * @return ResourceLocation for the Texture Atlas
     */
    default ResourceLocation getTextureAtlasLocation() {
        return PlayerContainer.LOCATION_BLOCKS_TEXTURE;
    }

    /**
     * Fetches Minecraft's Texture Manager
     * @return the TextureManager object
     */
    default TextureManager getTextureManager() {
        return Minecraft.getInstance().textureManager;
    }

    /**
     * Fetches Minecraft's Model Manager
     * @return the ModelManager object
     */
    default ModelManager getModelManager() {
        return Minecraft.getInstance().getModelManager();
    }

    /**
     * Fetches Minecraft's Item Renderer
     * @return the ItemRenderer object
     */
    default ItemRenderer getItemRenderer() {
        return Minecraft.getInstance().getItemRenderer();
    }

    /**
     * Fetches Minecraft's Entity Rendering Manager
     * @return the EntityRendererManager object
     */
    default EntityRendererManager getEntityRendererManager() {
        return Minecraft.getInstance().getRenderManager();
    }

    /**
     * Fetches the Player's current Camera Orientation
     * @return a Quaternion object representing the orientation of the camera
     */
    default Quaternion getCameraOrientation() {
        return this.getEntityRendererManager().getCameraOrientation();
    }

    /**
     * Fetches the Player's current Point of View (First Person, Third Person over shoulder, Third Person front)
     * @return the PointOfView object
     */
    default PointOfView getPointOfView() {
        return this.getEntityRendererManager().options.getPointOfView();
    }

    /**
     * Method to render the coordinate system for the current matrix. Renders three lines with
     * length 1 starting from (0, 0, 0): red line along x axis, green line along y axis and blue
     * line along z axis.
     */
    default void renderCoordinateSystem(MatrixStack transforms, IRenderTypeBuffer buffer) {
        IVertexBuilder builder = buffer.getBuffer(RenderType.getLines());
        Matrix4f matrix = transforms.getLast().getMatrix();
        // X-axis
        builder.pos(matrix, 0, 0, 0).color(255, 0, 0, 255).endVertex();
        builder.pos(matrix, 1, 0, 0).color(255, 0, 0, 255).endVertex();
        // Y-axis
        builder.pos(matrix, 0, 0, 0).color(0, 255, 0, 255).endVertex();
        builder.pos(matrix, 0, 1, 0).color(0, 255, 0, 255).endVertex();
        // Z-axis
        builder.pos(matrix, 0, 0, 0).color(0, 0, 255, 255).endVertex();
        builder.pos(matrix, 0, 0, 1).color(0, 0, 255, 255).endVertex();
    }

    /**
     * Internal class to store pointers to objects which need initialization
     */
    final class Objects {
        private Objects() {}

        private static TextureAtlasSprite missingSprite;
    }
}
