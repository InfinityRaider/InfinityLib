package com.infinityraider.infinitylib.render.fluid;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IFluidRenderer {
    void render(BlockAndTintGetter world, BlockPos pos, VertexConsumer builder, FluidState state);
}
