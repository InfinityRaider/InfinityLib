/*
 */
package com.infinityraider.infinitylib.render.tile;

import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author RlonRyan
 */
@FunctionalInterface
public interface ITesr<T extends TileEntity> {
	
	void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage);
	
}
