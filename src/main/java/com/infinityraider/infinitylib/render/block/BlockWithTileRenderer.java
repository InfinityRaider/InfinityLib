package com.infinityraider.infinitylib.render.block;

import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlockWithTile;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;
import com.infinityraider.infinitylib.render.tile.ITesr;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class BlockWithTileRenderer<B extends BlockBase & ICustomRenderedBlockWithTile<T>, T extends TileEntityBase> extends BlockRenderer<B> implements ITesr<T> {

    public BlockWithTileRenderer(ITileRenderingHandler<B, T> renderer) {
        super(renderer);
    }

    @Override
    public ITileRenderingHandler<B, T> getRenderer() {
        return (ITileRenderingHandler<B, T>) super.getRenderer();
    }

    @Override
    public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
        ITessellator tessellator = TessellatorVertexBuffer.getInstance();
        World world = te.getWorld();
        BlockPos pos = te.getPos();

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        tessellator.setColorRGBA(1, 1, 1, 1);

        this.getRenderer().renderWorldBlockDynamic(tessellator, world, pos, x, y, z, this.getBlock(), te, partialTicks, destroyStage);

        tessellator.draw();

        GL11.glTranslated(-x, -y, -z);
        GL11.glPopMatrix();

    }

}
