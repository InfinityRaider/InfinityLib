package com.infinityraider.infinitylib.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * TER wrapper class to allow for use of the ITesr interface.
 * This is a way to simulate multiple inheritance of renderers.
 */
@OnlyIn(Dist.CLIENT)
public class TileEntityRendererWrapper<T extends BlockEntity> implements BlockEntityRenderer<T> {
	
	private final ITileRenderer<T> tesr;

	@SuppressWarnings("unchecked")
	public static TileEntityRendererWrapper createWrapper(ITileRenderer renderer) {
		return new TileEntityRendererWrapper(renderer);
	}

	private TileEntityRendererWrapper(ITileRenderer<T> renderer) {
		this.tesr = renderer;
	}

	@Override
	public void render(T tile, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light, int overlay) {
		this.tesr.render(tile, partialTicks, transforms, buffer, light, overlay);
	}
}