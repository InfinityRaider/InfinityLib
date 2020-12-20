package com.infinityraider.infinitylib.render.tile;

import com.infinityraider.infinitylib.render.IRenderUtilities;
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
public interface ITileRenderer<T extends TileEntity> extends IRenderUtilities {
	
	void render(T tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay);
}