package com.infinityraider.infinitylib.render.tile;

import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Interface allowing TESRs to be used by classes that already have a superclass.
 */
@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface ITileRenderer<T extends BlockEntity> extends IRenderUtilities {
	
	void render(T tile, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light, int overlay);
}