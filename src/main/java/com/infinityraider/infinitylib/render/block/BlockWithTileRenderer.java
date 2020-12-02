package com.infinityraider.infinitylib.render.block;

import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlockWithTile;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;
import com.infinityraider.infinitylib.render.tile.ITesr;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.inventory.container.PlayerContainer.LOCATION_BLOCKS_TEXTURE;

public class BlockWithTileRenderer<B extends BlockBase & ICustomRenderedBlockWithTile<T>, T extends TileEntityBase> extends BlockRenderer<B> implements ITesr<T> {

    public BlockWithTileRenderer(ITileRenderingHandler<B, T> renderer) {
        super(renderer);
    }

    @Override
    public ITileRenderingHandler<B, T> getRenderer() {
        return (ITileRenderingHandler<B, T>) super.getRenderer();
    }

    @Override
    public void renderTileEntityAt(T tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        
        // Fetch the tessellator instance.
        ITessellator tessellator = TessellatorVertexBuffer.getInstance();
        
        // Fetch world information.
        World world = tile.getWorld();
        BlockPos pos = tile.getPos();

        // Bind the textures used for block rendering.
        Minecraft.getInstance().getTextureManager().bindTexture(LOCATION_BLOCKS_TEXTURE);

        // Save the translation matrix.
        GlStateManager.pushMatrix();
        GlStateManager.pushTextureAttributes();
        GlStateManager.pushLightingAttributes();
        tessellator.pushMatrix();
        
        // Translate to the location to render at.
        tessellator.translate(tile.getPos());
        
        // Reset the tessellator's drawing color.
        tessellator.setColorRGBA(1, 1, 1, 1);

        // Set the light
        tessellator.setBrightness(combinedLight);

        // Start the tesse;lator drawing quads.
        tessellator.startDrawingQuads(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

        // Call the block renderer to render the block dynamically.
        this.getRenderer().renderWorldBlockDynamic(tessellator, world, pos, this.getBlock(), tile, partialTicks);

        // Finish tessellator drawing quads.
        tessellator.draw();

        // Restore the translation matrix.
        tessellator.popMatrix();
        GlStateManager.popAttributes();
        GlStateManager.popMatrix();

    }
}
