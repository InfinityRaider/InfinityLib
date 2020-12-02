package com.infinityraider.infinitylib.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * TESR wrapper class to allow for use of the ITesr interface.
 * This is a way to simulate multiple inheritance of renderers.
 */
@OnlyIn(Dist.CLIENT)
public class TesrWrapper<T extends TileEntity> extends TileEntityRenderer<T> {
	
	private final ITesr<T> tesr;

	public TesrWrapper(TileEntityRendererDispatcher dispatcher, ITesr<T> tesr) {
		super(dispatcher);
		this.tesr = tesr;
	}

	@Override
	public void render(T tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		this.tesr.renderTileEntityAt(tile, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
	}
}