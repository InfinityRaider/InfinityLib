package com.infinityraider.infinitylib.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * TER wrapper class to allow for use of the ITesr interface.
 * This is a way to simulate multiple inheritance of renderers.
 */
@OnlyIn(Dist.CLIENT)
public class TileEntityRendererWrapper<T extends TileEntity> extends TileEntityRenderer<T> {
	
	private final ITileRenderer<T> tesr;

	@SuppressWarnings("unchecked")
	public static TileEntityRendererWrapper createWrapper(TileEntityRendererDispatcher dispatcher, ITileRenderer renderer) {
		return new TileEntityRendererWrapper(dispatcher, renderer);
	}

	private TileEntityRendererWrapper(TileEntityRendererDispatcher dispatcher, ITileRenderer<T> renderer) {
		super(dispatcher);
		this.tesr = renderer;
	}

	@Override
	public void render(T tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay) {
		this.tesr.render(tile, partialTicks, transforms, buffer, light, overlay);
	}
}