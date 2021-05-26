package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.property.InfProperty;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface IFluidLoggable extends IBucketPickupHandler, ILiquidContainer {
    @Override
    default boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
        return InfProperty.Defaults.fluidlogged().fetch(state).isEmpty() && InfProperty.FluidLogged.accepts(fluid);
    }

    @Override
    default boolean receiveFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluid) {
        if (this.canContainFluid(world, pos, state, fluid.getFluid())) {
            if (!world.isRemote()) {
                world.setBlockState(pos, InfProperty.Defaults.fluidlogged().apply(state, InfProperty.FluidLogged.get(fluid)), 3);
                world.getPendingFluidTicks().scheduleTick(pos, fluid.getFluid(), fluid.getFluid().getTickRate(world));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    default Fluid pickupFluid(IWorld world, BlockPos pos, BlockState state) {
        InfProperty.FluidLogged fluid = InfProperty.Defaults.fluidlogged().fetch(state);
        if (fluid.isEmpty()) {
            return Fluids.EMPTY;
        } else {
            world.setBlockState(pos, InfProperty.Defaults.fluidlogged().apply(state), 3);
            return fluid.getFluid();
        }
    }
}
