package com.infinityraider.infinitylib.render.fluid;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IFluidRenderer {
    void render(IBlockDisplayReader world, BlockPos pos, IVertexBuilder builder, FluidState state);
}
