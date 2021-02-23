package com.infinityraider.infinitylib.render;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.Color;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;

/**
 * Interface with utility methods which can make life easier when rendering stuff.
 * Implement in any render class to gain access to these methods.
 */
@OnlyIn(Dist.CLIENT)
public interface IRenderUtilities {
    /**
     *
     */
    default TessellatorBakedQuad getQuadTessellator() {
        return new TessellatorBakedQuad();
    }

    /**
     * Renders an item
     */
    default void renderItem(ItemStack stack, ItemCameraTransforms.TransformType perspective, int light,
                            MatrixStack transforms, IRenderTypeBuffer buffer) {
        this.renderItem(stack, perspective, light, OverlayTexture.NO_OVERLAY, transforms, buffer);
    }

    /**
     * Renders an item
     */
    default void renderItem(ItemStack stack, ItemCameraTransforms.TransformType perspective, int light, int overlay,
                            MatrixStack transforms, IRenderTypeBuffer buffer) {
        this.getItemRenderer().renderItem(stack, perspective, light, overlay, transforms, buffer);
    }

    /**
     * Renders a block state
     */
    default boolean renderBlockState(BlockState state, MatrixStack transforms, IVertexBuilder buffer) {
        return this.renderBlockState(state, Objects.DEFAULT_POS, transforms, buffer, OverlayTexture.NO_OVERLAY);
    }

    /**
     * Renders a block state
     */
    default boolean renderBlockState(BlockState state, BlockPos pos, MatrixStack transforms, IVertexBuilder buffer, int overlay) {
        World world =InfinityLib.instance.getClientWorld();
        return this.renderBlockModel(world, this.getModelForState(state), state, pos,
                transforms, buffer, false, world.getRandom(), state.getPositionRandom(pos), overlay, EmptyModelData.INSTANCE);
    }

    /**
     * Renders a block model
     */
    default boolean renderBlockModel(IBlockDisplayReader world, IBakedModel model, BlockState state, BlockPos pos, MatrixStack transforms,
                                     IVertexBuilder buffer, boolean checkSides, Random random, long rand, int overlay, IModelData modelData) {
        return this.getBlockRenderer().renderModel(world, model, state, pos, transforms, buffer, checkSides, random, rand, overlay, modelData);
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
     *
     * @param location the Resource Location
     * @return the sprite
     */
    default TextureAtlasSprite getSprite(ResourceLocation location) {
        return this.getTextureAtlas().getSprite(location);
    }

    /**
     * Converts a string to a ResourceLocation
     * @param string the String
     * @return the ResourceLocation
     */
    default ResourceLocation getResourceLocation(String string) {
        return new ResourceLocation(string);
    }

    /**
     * Converts a String to a RenderMaterial for the Block Atlas
     * @param string the String
     * @return the RenderMaterial
     */
    default RenderMaterial getRenderMaterial(String string) {
        return this.getRenderMaterial(this.getResourceLocation(string));
    }

    /**
     * Converts a ResourceLocation to a RenderMaterial for the Block Atlas
     * @param texture the ResourceLocation
     * @return the RenderMaterial
     */
    default RenderMaterial getRenderMaterial(ResourceLocation texture) {
        return new RenderMaterial(this.getTextureAtlasLocation(), texture);
    }

    /**
     * Binds a texture for rendering
     *
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
     *
     * @return the AtlasTexture object
     */
    default AtlasTexture getTextureAtlas() {
        return this.getModelManager().getAtlasTexture(this.getTextureAtlasLocation());
    }

    /**
     * Fetches the ResourceLocation associated with the Texture Atlas
     *
     * @return ResourceLocation for the Texture Atlas
     */
    default ResourceLocation getTextureAtlasLocation() {
        return PlayerContainer.LOCATION_BLOCKS_TEXTURE;
    }

    /**
     * Fetches Minecraft's Texture Manager
     *
     * @return the TextureManager object
     */
    default TextureManager getTextureManager() {
        return Minecraft.getInstance().textureManager;
    }

    /**
     * Fetches Minecraft's Model Manager
     *
     * @return the ModelManager object
     */
    default ModelManager getModelManager() {
        return Minecraft.getInstance().getModelManager();
    }

    /**
     * Fetches Minecraft's Block Renderer Dispatcher
     *
     * @return the BlockRendererDispatcher object
     */
    default BlockRendererDispatcher getBlockRendererDispatcher() {
        return Minecraft.getInstance().getBlockRendererDispatcher();
    }

    /**
     * Fetches Minecraft's Block Model Renderer
     *
     * @return the BlockModelRenderer object
     */
    default BlockModelRenderer getBlockRenderer() {
        return this.getBlockRendererDispatcher().getBlockModelRenderer();
    }

    /**
     * Fetches the IBakedModel for a BlockState
     *
     * @param state the BlockState
     * @return the IBakedModel
     */
    default IBakedModel getModelForState(BlockState state) {
        return this.getBlockRendererDispatcher().getModelForState(state);
    }

    /**
     * Fetches Minecraft's Item Renderer
     *
     * @return the ItemRenderer object
     */
    default ItemRenderer getItemRenderer() {
        return Minecraft.getInstance().getItemRenderer();
    }

    /**
     * @return an ItemModelGenerator object
     */
    default ItemModelGenerator getItemModelGenerator() {
        return Objects.ITEM_MODEL_GENERATOR;
    }

    /**
     * Fetches Minecraft's Entity Rendering Manager
     *
     * @return the EntityRendererManager object
     */
    default EntityRendererManager getEntityRendererManager() {
        return Minecraft.getInstance().getRenderManager();
    }


    /**
     * Fetches Minecraft's Font Renderer
     *
     * @return the FontRenderer object
     */
    default FontRenderer getFontRenderer() {
        return Minecraft.getInstance().fontRenderer;
    }

    /**
     * Fetches the Player's current Camera Orientation
     *
     * @return a Quaternion object representing the orientation of the camera
     */
    default Quaternion getCameraOrientation() {
        return this.getEntityRendererManager().getCameraOrientation();
    }

    /**
     * Fetches the Player's current Point of View (First Person, Third Person over shoulder, Third Person front)
     *
     * @return the PointOfView object
     */
    default PointOfView getPointOfView() {
        return this.getEntityRendererManager().options.getPointOfView();
    }

    /**
     * @return The width in pixels of the Minecraft window
     */
    default int getScaledWindowWidth() {
        return Minecraft.getInstance().getMainWindow().getScaledWidth();
    }
    /**
     * @return The height in pixels of the Minecraft window
     */
    default int getScaledWindowHeight() {
        return Minecraft.getInstance().getMainWindow().getScaledHeight();
    }

    /**
     * @return The render type buffer implementation
     */
    default IRenderTypeBuffer.Impl getRenderTypeBuffer() {
        return  Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
    }

    /**
     * Fetches a vertex builder for a RenderType, fetches the IRenderTypeBuffer internally
     * @param renderType the RenderType
     * @return an IVertexBuilder
     */
    default IVertexBuilder getVertexBuilder(RenderType renderType) {
        return this.getVertexBuilder(this.getRenderTypeBuffer(), renderType);
    }

    /**
     * Fetches a vertex builder from an IRenderTypeBuffer for a RenderType
     * @param buffer the IRenderTypeBuffer
     * @param renderType the RenderType
     * @return an IVertexBuilder
     */
    default IVertexBuilder getVertexBuilder(IRenderTypeBuffer buffer, RenderType renderType) {
        return buffer.getBuffer(renderType);
    }

    default Color convertColor(Vector3f color) {
        return Color.fromInt(this.calculateColor(color));
    }

    default int calculateColor(Vector3f color) {
        return this.calculateColor(color.getX(), color.getX(), color.getZ());
    }

    default int calculateColor(float r, float g, float b) {
        return this.calculateColor((int) (255 * r), (int) (255 * g),(int) (255 * b));
    }

    default int calculateColor(int r, int g, int b) {
        return (65536 * r) + (256 * g) + b;
    }

    /**
     * Method to render the coordinate system for the current matrix. Renders three lines with
     * length 1 starting from (0, 0, 0): red line along x axis, green line along y axis and blue
     * line along z axis.
     */
    default void renderCoordinateSystem(MatrixStack transforms, IRenderTypeBuffer buffer) {
        IVertexBuilder builder = this.getVertexBuilder(buffer, RenderType.getLines());
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
        private Objects() {
        }

        private static TextureAtlasSprite missingSprite;

        private static final BlockPos DEFAULT_POS = new BlockPos(0,0,0);

        private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();

        public static boolean equals(Object a, Object b) {
            return java.util.Objects.equals(a, b);
        }
    }
}
