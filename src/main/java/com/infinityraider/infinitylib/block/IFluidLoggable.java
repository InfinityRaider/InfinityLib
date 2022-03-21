package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.property.InfProperty;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface IFluidLoggable extends BucketPickup, LiquidBlockContainer {
    @Override
    default boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return InfProperty.Defaults.fluidlogged().fetch(state).isEmpty() && InfProperty.FluidLogged.accepts(fluid);
    }

    @Override
    default boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluid) {
        if (this.canPlaceLiquid(world, pos, state, fluid.getType())) {
            if (!world.isClientSide()) {
                world.setBlock(pos, InfProperty.Defaults.fluidlogged().apply(state, InfProperty.FluidLogged.get(fluid)), 3);
                world.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(world));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    default ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState state) {
        InfProperty.FluidLogged fluid = InfProperty.Defaults.fluidlogged().fetch(state);
        if (fluid.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            world.setBlock(pos, InfProperty.Defaults.fluidlogged().apply(state), 3);
            return FluidUtil.getFilledBucket(new FluidStack(fluid.getFluid(), FluidAttributes.BUCKET_VOLUME));
        }
    }

    @Override
    default Optional<SoundEvent> getPickupSound(BlockState state) {
        InfProperty.FluidLogged fluid = InfProperty.Defaults.fluidlogged().fetch(state);
        if(fluid.isEmpty()) {
            return BucketPickup.super.getPickupSound(state);
        } else {
            return fluid.getFluid().getPickupSound();
        }
    }
}
