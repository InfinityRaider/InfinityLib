package com.infinityraider.infinitylib.render;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
     * Returns a new tessellator object to tessellate baked quads with
     */
    default ITessellator getBakedQuadTessellator() {
        return new TessellatorBakedQuad();
    }

    /**
     * Returns a new tessellator object to tessellate directly to the vertex buffer
     */
    default ITessellator getVertexBufferTessellator(MultiBufferSource.BufferSource buffer, RenderType renderType) {
        return new TessellatorVertexBuffer(buffer, renderType);
    }

    /**
     * @return a RNG if none other are available
     */
    default Random getRandom() {
        return Objects.RANDOM;
    }

    /**
     * Renders an item
     */
    default void renderItem(ItemStack stack, ItemTransforms.TransformType perspective, int light,
                            PoseStack transforms, MultiBufferSource buffer) {
        this.renderItem(stack, perspective, light, OverlayTexture.NO_OVERLAY, transforms, buffer);
    }

    /**
     * Renders an item
     */
    default void renderItem(ItemStack stack, ItemTransforms.TransformType perspective, int light, int overlay,
                            PoseStack transforms, MultiBufferSource buffer) {
        this.getItemRenderer().renderStatic(stack, perspective, light, overlay, transforms, buffer, 0);
    }

    /**
     * Renders a block state
     */
    default boolean renderBlockState(BlockState state, PoseStack transforms, VertexConsumer buffer) {
        return this.renderBlockState(state, Objects.DEFAULT_POS, transforms, buffer, OverlayTexture.NO_OVERLAY);
    }

    /**
     * Renders a block state
     */
    default boolean renderBlockState(BlockState state, BlockPos pos, PoseStack transforms, VertexConsumer buffer, int overlay) {
        Level world =InfinityLib.instance.getClientWorld();
        return this.renderBlockModel(world, this.getModelForState(state), state, pos,
                transforms, buffer, false, world.getRandom(), state.getSeed(pos), overlay, EmptyModelData.INSTANCE);
    }

    /**
     * Renders a block model
     */
    default boolean renderBlockModel(BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos, PoseStack transforms,
                                     VertexConsumer buffer, boolean checkSides, Random random, long rand, int overlay, IModelData modelData) {
        return this.getBlockRenderer().tesselateBlock(world, model, state, pos, transforms, buffer, checkSides, random, rand, overlay, modelData);
    }

    /**
     * @return the TextureAtlasSprite for the missing texture
     */
    default TextureAtlasSprite getMissingSprite() {
        if (Objects.missingSprite == null) {
            Objects.missingSprite = this.getSprite(MissingTextureAtlasSprite.getLocation());
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
     * Fetches the sprite on a Texture Atlas related to a render material
     *
     * @param material the render material
     * @return the sprite
     */
    default TextureAtlasSprite getSprite(Material material) {
        return this.getTextureAtlas(material.atlasLocation()).getSprite(material.texture());
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
    default Material getRenderMaterial(String string) {
        return this.getRenderMaterial(this.getResourceLocation(string));
    }

    /**
     * Converts a ResourceLocation to a RenderMaterial for the Block Atlas
     * @param texture the ResourceLocation
     * @return the RenderMaterial
     */
    default Material getRenderMaterial(ResourceLocation texture) {
        return new Material(this.getTextureAtlasLocation(), texture);
    }

    /**
     * Binds a texture for rendering
     *
     * @param location the ResourceLocation for the texture
     */
    default void bindTexture(ResourceLocation location) {
        RenderSystem.setShaderTexture(0, location);
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
    default TextureAtlas getTextureAtlas() {
        return this.getTextureAtlas(this.getTextureAtlasLocation());
    }

    /**
     * Fetches the AtlasTexture object representing the Texture Atlas
     *
     * @param location the location for the atlas
     * @return the AtlasTexture object
     */
    default TextureAtlas getTextureAtlas(ResourceLocation location) {
        return this.getModelManager().getAtlas(location);
    }

    /**
     * Fetches the ResourceLocation associated with the Texture Atlas
     *
     * @return ResourceLocation for the Texture Atlas
     */
    default ResourceLocation getTextureAtlasLocation() {
        return InventoryMenu.BLOCK_ATLAS;
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
    default BlockRenderDispatcher getBlockRendererDispatcher() {
        return Minecraft.getInstance().getBlockRenderer();
    }

    /**
     * Fetches Minecraft's Block Model Renderer
     *
     * @return the BlockModelRenderer object
     */
    default ModelBlockRenderer getBlockRenderer() {
        return this.getBlockRendererDispatcher().getModelRenderer();
    }

    /**
     * Fetches the IBakedModel for a BlockState
     *
     * @param state the BlockState
     * @return the IBakedModel
     */
    default BakedModel getModelForState(BlockState state) {
        return this.getBlockRendererDispatcher().getBlockModel(state);
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
    default EntityRenderDispatcher getEntityRendererManager() {
        return Minecraft.getInstance().getEntityRenderDispatcher();
    }


    /**
     * Fetches Minecraft's Font Renderer
     *
     * @return the FontRenderer object
     */
    default Font getFontRenderer() {
        return Minecraft.getInstance().font;
    }

    /**
     * Fetches the Player's current Camera Orientation
     *
     * @return a Quaternion object representing the orientation of the camera
     */
    default Quaternion getCameraOrientation() {
        return this.getEntityRendererManager().cameraOrientation();
    }

    /**
     * Fetches the Player's current Point of View (First Person, Third Person over shoulder, Third Person front)
     *
     * @return the PointOfView object
     */
    default CameraType getPointOfView() {
        return this.getEntityRendererManager().options.getCameraType();
    }

    /**
     * @return The width in pixels of the Minecraft window
     */
    default int getScaledWindowWidth() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }
    /**
     * @return The height in pixels of the Minecraft window
     */
    default int getScaledWindowHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    /**
     * @return The render type buffer implementation
     */
    default MultiBufferSource.BufferSource getRenderTypeBuffer() {
        return  Minecraft.getInstance().renderBuffers().bufferSource();
    }

    /**
     * Fetches a vertex builder for a RenderType, fetches the IRenderTypeBuffer internally
     * @param renderType the RenderType
     * @return an IVertexBuilder
     */
    default VertexConsumer getVertexBuilder(RenderType renderType) {
        return this.getVertexBuilder(this.getRenderTypeBuffer(), renderType);
    }

    /**
     * Fetches a vertex builder from an IRenderTypeBuffer for a RenderType
     * @param buffer the IRenderTypeBuffer
     * @param renderType the RenderType
     * @return an IVertexBuilder
     */
    default VertexConsumer getVertexBuilder(MultiBufferSource buffer, RenderType renderType) {
        return buffer.getBuffer(renderType);
    }

    default TextColor convertColor(Vector3f color) {
        return TextColor.fromRgb(this.calculateColor(color));
    }

    default int calculateColor(Vector3f color) {
        return this.calculateColor(color.x(), color.y(), color.z());
    }

    default int calculateColor(float r, float g, float b) {
        return this.calculateColor((int) (255 * r), (int) (255 * g),(int) (255 * b));
    }

    default int calculateColor(int r, int g, int b) {
        return (65536 * r) + (256 * g) + b;
    }

    default float getPartialTick() {
        return Minecraft.getInstance().getFrameTime();
    }

    /**
     * Method to render the coordinate system for the current matrix. Renders three lines with
     * length 1 starting from (0, 0, 0): red line along x axis, green line along y axis and blue
     * line along z axis.
     */
    default void renderCoordinateSystem(PoseStack transforms, MultiBufferSource buffer) {
        VertexConsumer builder = this.getVertexBuilder(buffer, RenderType.lines());
        Matrix4f matrix = transforms.last().pose();
        // X-axis
        builder.vertex(matrix, 0, 0, 0).color(255, 0, 0, 255).endVertex();
        builder.vertex(matrix, 1, 0, 0).color(255, 0, 0, 255).endVertex();
        // Y-axis
        builder.vertex(matrix, 0, 0, 0).color(0, 255, 0, 255).endVertex();
        builder.vertex(matrix, 0, 1, 0).color(0, 255, 0, 255).endVertex();
        // Z-axis
        builder.vertex(matrix, 0, 0, 0).color(0, 0, 255, 255).endVertex();
        builder.vertex(matrix, 0, 0, 1).color(0, 0, 255, 255).endVertex();
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

        private static final Random RANDOM = new Random();

        public static boolean equals(Object a, Object b) {
            return java.util.Objects.equals(a, b);
        }
    }
}
