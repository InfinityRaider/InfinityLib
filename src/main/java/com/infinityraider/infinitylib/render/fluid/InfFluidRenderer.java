package com.infinityraider.infinitylib.render.fluid;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.fluid.IInfinityFluid;
import com.infinityraider.infinitylib.utility.UnsafeUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class InfFluidRenderer extends LiquidBlockRenderer {
    // Previous FluidBlockRenderer object, calls are forwarded to this, so we don't break someone else's shit
    // in case they also injected their own fluid renderer
    private final LiquidBlockRenderer previous;

    private InfFluidRenderer(LiquidBlockRenderer previous) {
        this.previous = previous;
    }

    @Override
    public boolean tesselate(BlockAndTintGetter world, BlockPos pos, VertexConsumer builder, BlockState block, FluidState fluid) {
        if(fluid.getType() instanceof IInfinityFluid) {
            IFluidRenderer renderer = ((IInfinityFluid) fluid.getType()).getRenderer();
            if(renderer != null) {
                renderer.render(world, pos, builder, fluid);
                return true;
            }
        }
        return this.previous.tesselate(world, pos, builder, block, fluid);
    }

    // Method to inject our own fluid renderer into vanilla's
    public static void init() {
        InfinityLib.instance.getLogger().info("Trying to inject Fluid Renderer");
        Arrays.stream(BlockRenderDispatcher.class.getDeclaredFields())
                .filter(field -> field.getType().isAssignableFrom(LiquidBlockRenderer.class))
                .findAny()
                .map(field -> {
                    try {
                        // Set accessible
                        field.setAccessible(true);
                        // Fetch previous value
                        LiquidBlockRenderer previous = (LiquidBlockRenderer) field.get(Minecraft.getInstance().getBlockRenderer());
                        // Set field
                        InfFluidRenderer renderer = new InfFluidRenderer(previous);
                        boolean success = UnsafeUtil.getInstance().replaceField(field, Minecraft.getInstance().getBlockRenderer(), renderer);
                        // Return
                        if(success) {
                            InfinityLib.instance.getLogger().info("Successfully injected fluid renderer");
                            return true;
                        } else {
                            InfinityLib.instance.getLogger().info("Fluid Renderer injection failed");
                        }
                    } catch(Exception e) {
                        // this might happen
                        InfinityLib.instance.getLogger().info("Fluid Renderer injection failed");
                        e.printStackTrace();
                    }
                    return false;
                })
                .orElseGet(() -> {
                    // this should never happen but still has to be here to make the code compile
                    InfinityLib.instance.getLogger().info("No Fluid Renderer object found, very strange");
                    return false;
                });
    }

}
