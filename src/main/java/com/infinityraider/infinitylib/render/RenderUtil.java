package com.infinityraider.infinitylib.render;

import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;

import static net.minecraft.inventory.container.PlayerContainer.LOCATION_BLOCKS_TEXTURE;

/**
 * Utility base class for rendering
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public abstract class RenderUtil {

    private static TextureAtlasSprite missingSprite;

    public static TextureAtlasSprite getMissingSprite() {
        if (missingSprite == null) {
            missingSprite = Minecraft.getInstance().getModelManager()
                    .getAtlasTexture(LOCATION_BLOCKS_TEXTURE)
                    .getSprite(MissingTextureSprite.getLocation());
        }
        return missingSprite;
    }

    public static void drawBlockModel(ITessellator tessellator, BlockState state, Random random) {
        Minecraft.getInstance().getTextureManager().bindTexture(LOCATION_BLOCKS_TEXTURE);
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        for (Direction dir : Direction.values()) {
            drawQuads(TessellatorVertexBuffer.getInstance(), model.getQuads(state, dir, random));
        }
        drawQuads(TessellatorVertexBuffer.getInstance(), model.getQuads(state, null, random));
    }

    public static void drawQuads(ITessellator tessellator, List<BakedQuad> quads) {
        if (quads.size() > 0) {
            tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
            tessellator.addQuads(quads);
            tessellator.draw();
        }
    }

    public static int getMixedBrightness(World world, double x, double y, double z) {
        return getMixedBrightness(world, new BlockPos(x, y, z));
    }

    public static int getMixedBrightness(World world, BlockPos pos) {
        return getMixedBrightness(world, pos, world.getBlockState(pos));
    }

    public static int getMixedBrightness(World world, BlockPos pos, Block block) {
        return getMixedBrightness(world, pos, world.getBlockState(pos), block);
    }

    public static int getMixedBrightness(World world, BlockPos pos, BlockState state) {
        return getMixedBrightness(world, pos, state, state.getBlock());
    }

    public static int getMixedBrightness(World world, BlockPos pos, BlockState state, Block block) {
        return WorldRenderer.getCombinedLight(world, pos);
    }

    public static void rotateToDirection(ITessellator tess, Direction dir) {
        switch (dir) {
            case WEST:
                tess.translate(0.5f, 0, 0.5f);
                tess.rotate(270, 0, 1, 0);
                tess.translate(-0.5f, 0, -0.5f);
                break;
            case NORTH:
                tess.translate(0.5f, 0, 0.5f);
                tess.rotate(180, 0, 1, 0);
                tess.translate(-0.5f, 0, -0.5f);
                break;
            case EAST:
                tess.translate(0.5f, 0, 0.5f);
                tess.rotate(90, 0, 1, 0);
                tess.translate(-0.5f, 0, -0.5f);
                break;
            case DOWN:
                tess.translate(0, 0.5f, 0.5f);
                tess.rotate(90, 1, 0, 0);
                tess.translate(0, -0.5f, -0.5f);
                break;
            case UP:
                tess.translate(0, 0.5f, 0.5f);
                tess.rotate(270, 1, 0, 0);
                tess.translate(0, -0.5f, -0.5f);
                break;
        }
    }

    public static void renderItemStack(World world, ItemStack stack, double x, double y, double z, double scale, boolean rotate) {
        // Save Settings
        GlStateManager.pushLightingAttributes();
        GlStateManager.pushTextureAttributes();
        GlStateManager.pushMatrix();

        // Fix Lighting
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableLighting();

        // Initialize transformation
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();

        // Translate to correct spot
        matrixStack.translate(x, y, z);

        // Scale to correct Size
        matrixStack.scale((float) scale, (float) scale, (float) scale);

        // Rotate Item as function of system time.
        if (rotate) {
            double angle = (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
            matrixStack.rotate(new Quaternion(new Vector3f(0, 1, 0), (float) angle, true));
        }

        // Draw the item.
        Minecraft.getInstance().getItemRenderer().renderItem(stack,
                ItemCameraTransforms.TransformType.GROUND,
                getMixedBrightness(world, x, y,z),
                OverlayTexture.NO_OVERLAY,
                matrixStack,
                Minecraft.getInstance().getRenderTypeBuffers().getBufferSource());

        // Pop matrix stack
        matrixStack.pop();

        // Restore Settings
        GlStateManager.popAttributes();
        GlStateManager.popMatrix();
    }

    /**
     * Method to cancel out view bobbing when rendering from RenderHandEvent
     *
     * @param player player
     * @param partialTicks partial tick
     * @param inverse inverse or not
     */
    public static final void correctViewBobbing(PlayerEntity player, MatrixStack stack, float partialTicks, boolean inverse) {
        if (!Minecraft.getInstance().gameSettings.viewBobbing) {
            return;
        }
        float f = player.distanceWalkedModified - player.prevDistanceWalkedModified;
        float f1 = -(player.distanceWalkedModified + f * partialTicks);
        float f2 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * partialTicks;
        float f3 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        if (inverse) {
            stack.translate(MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5F, -Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2), 0.0F);
            stack.rotate(new Quaternion(new Vector3f(0, 0, 1), MathHelper.sin(f1 * (float) Math.PI) * f2 * 3.0F,true));
            stack.rotate(new Quaternion(new Vector3f(1, 0, 0), f3 + Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F,true));
            stack.pop();
        } else {
            stack.push();
            stack.rotate(new Quaternion(new Vector3f(1, 0, 0), -f3 - Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F,true));
            stack.rotate(new Quaternion(new Vector3f(0, 0, 1), -MathHelper.sin(f1 * (float) Math.PI) * f2 * 3.0F,true));
            stack.translate(-MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5F, Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2), 0.0F);
        }
    }

    /**
     * Method to render the coordinate system for the current matrix. Renders three lines with
     * length 1 starting from (0, 0, 0): red line along x axis, green line along y axis and blue
     * line along z axis.
     */
    public static final void renderCoordinateSystemDebug() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();

        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 16; i++) {
            buffer.pos(((float) i) / 16.0F, 0, 0).color(255, 0, 0, 255).endVertex();
        }
        tessellator.draw();

        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 16; i++) {
            buffer.pos(0, ((float) i) / 16.0F, 0).color(0, 255, 0, 255).endVertex();
        }
        tessellator.draw();

        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 16; i++) {
            buffer.pos(0, 0, ((float) i) / 16.0F).color(0, 0, 255, 255).endVertex();
        }
        tessellator.draw();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
    }
}
