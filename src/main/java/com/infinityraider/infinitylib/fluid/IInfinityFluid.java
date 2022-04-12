package com.infinityraider.infinitylib.fluid;

import com.infinityraider.infinitylib.render.fluid.IFluidRenderer;
import com.infinityraider.infinitylib.utility.registration.IInfinityRegistrable;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface IInfinityFluid extends IInfinityRegistrable<Fluid> {

    @Nullable
    @OnlyIn(Dist.CLIENT)
    default IFluidRenderer getRenderer() {
        return null;
    }
}
