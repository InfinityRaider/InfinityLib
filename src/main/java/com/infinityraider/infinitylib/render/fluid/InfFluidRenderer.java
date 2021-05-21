package com.infinityraider.infinitylib.render.fluid;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.fluid.IInfinityFluid;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.FluidBlockRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class InfFluidRenderer extends FluidBlockRenderer {
    // Previous FluidBlockRenderer object, calls are forwarded to this, so we don't break someone else's shit
    // in case they also injected their own fluid renderer
    private final FluidBlockRenderer previous;

    private InfFluidRenderer(FluidBlockRenderer previous) {
        this.previous = previous;
    }

    @Override
    public boolean render(IBlockDisplayReader world, BlockPos pos, IVertexBuilder builder, FluidState state) {
        if(state.getFluid() instanceof IInfinityFluid) {
            IFluidRenderer renderer = ((IInfinityFluid) state.getFluid()).getRenderer();
            if(renderer != null) {
                renderer.render(world, pos, builder, state);
                return true;
            }
        }
        return this.previous.render(world, pos, builder, state);
    }

    // Method to inject our own fluid renderer into vanilla's
    public static void init() {
        InfinityLib.instance.getLogger().info("Trying to inject Fluid Renderer");
        Arrays.stream(BlockRendererDispatcher.class.getDeclaredFields())
                .filter(field -> field.getType().isAssignableFrom(FluidBlockRenderer.class))
                .findAny()
                .map(field -> {
                    try {
                        // Set accessible
                        field.setAccessible(true);
                        // Remove final modifier
                        Field modifiers = Field.class.getDeclaredField("modifiers");
                        modifiers.setAccessible(true);
                        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                        // Fetch previous value
                        FluidBlockRenderer previous = (FluidBlockRenderer) field.get(Minecraft.getInstance().getBlockRendererDispatcher());
                        // Set field
                        InfFluidRenderer renderer = new InfFluidRenderer(previous);
                        field.set(Minecraft.getInstance().getBlockRendererDispatcher(), renderer);
                        InfinityLib.instance.getLogger().info("Successfully injected fluid renderer");
                    } catch(Exception e) {
                        // this might happen
                        InfinityLib.instance.getLogger().info("Fluid Renderer injection failed");
                        e.printStackTrace();
                    }
                    return true;
                })
                .orElseGet(() -> {
                    // this should never happen but still has to be here to make the code compile
                    InfinityLib.instance.getLogger().info("No Fluid Renderer object found, very strange");
                    return false;
                });
    }

}
