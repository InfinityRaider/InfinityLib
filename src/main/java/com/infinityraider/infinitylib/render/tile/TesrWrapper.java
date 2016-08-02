/*
 */
package com.infinityraider.infinitylib.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author RlonRyan
 */
public class TesrWrapper<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
	
	private final ITesr<T> tesr;

	public TesrWrapper(ITesr<T> tesr) {
		this.tesr = tesr;
	}

	@Override
	public void renderTileEntityAt(T tile, double x, double y, double z, float partialTicks, int destroyStage) {
		this.tesr.renderTileEntityAt(tile, x, y, z, partialTicks, destroyStage);
	}
	
}
