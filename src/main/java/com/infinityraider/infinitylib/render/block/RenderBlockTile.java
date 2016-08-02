/*
 */
package com.infinityraider.infinitylib.render.block;

import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.block.blockstate.IBlockStateSpecial;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;
import com.infinityraider.infinitylib.render.tile.ITesr;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author RlonRyan
 */
public abstract class RenderBlockTile<B extends BlockBase & ICustomRenderedBlock, T extends TileEntityBase> extends RenderBlock<B> implements ITesr<T> {

	private final T dummy;
	private final boolean dynamicRender;

	public RenderBlockTile(B block, T dummy, boolean inventoryRender, boolean staticRender, boolean dynamicRender) {
		super(block, inventoryRender, dynamicRender);
		this.dummy = dummy;
		this.dynamicRender = dynamicRender;
	}
	
	/**
     * Gets the TileEntity for this renderer (this should be a new TileEntity which is not physically in a World),
     * it is used for registering this renderer and inventory rendering.
     * The class from this TileEntity and the object passed are saved and referenced,
     * The TileEntity is passed to this renderer for inventory rendering, it is not in a world so you can directly change fields to render it
     * This method may return null if there is no tile entity
     *
     * @return a new TileEntity for this renderer
     */
    public final T getTileEntity() {
		return dummy;
	}

	@Override
	public final void renderStatic(ITessellator tess, IBlockAccess world, IBlockState state, BlockPos pos) {
		if((state instanceof IBlockStateSpecial)) {
				IBlockStateSpecial<T, ? extends IBlockState> bs = ((IBlockStateSpecial<T, ? extends IBlockState>) state);
				renderStaticTile(tess, bs.getTileEntity());
		}
	}
	
	protected void renderStaticTile(ITessellator tess, T tile){
	}

	protected void renderDynamicTile(ITessellator tess, T tile, float partialTicks, int destroyStage){
	}
	
	/**
     * Return true from here to have this renderer have dynamic behaviour,
     * meaning the vertex buffer is reloaded every render tick (TESR behaviour).
     * If the renderer has dynamic behaviour, getTileEntity() should not return null.
     *
     * @return if this renderer has dynamic rendering behaviour
     */
    public final boolean hasDynamicRendering() {
		return this.dynamicRender;
	}
	
	@Override
    public final void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
        ITessellator tessellator = TessellatorVertexBuffer.getInstance();
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        IBlockState state = world.getBlockState(pos);
        IBlockState extendedState = state.getBlock().getExtendedState(state, world, pos);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        tessellator.setColorRGBA(255, 255, 255, 255);

		this.renderDynamicTile(tessellator, te, partialTicks, destroyStage);

        tessellator.draw();

        GL11.glTranslated(-x, -y, -z);
        GL11.glPopMatrix();
    }
	
}
