/*
 */
package com.infinityraider.infinitylib.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Interface allowing TESRs to be used by classes that already have a superclass.
 */
@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface ITesr<T extends TileEntity> {
	
	void renderTileEntityAt(T tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay);
	
}