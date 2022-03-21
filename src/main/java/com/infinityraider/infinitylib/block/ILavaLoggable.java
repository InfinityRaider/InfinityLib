package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.property.InfProperty;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface ILavaLoggable extends BucketPickup, LiquidBlockContainer {
    @Override
    default boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return !InfProperty.Defaults.lavalogged().fetch(state) && fluid == Fluids.LAVA;
    }

    @Override
    default boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluid) {
        if (this.canPlaceLiquid(world, pos, state, fluid.getType())) {
            if (!world.isClientSide()) {
                world.setBlock(pos, InfProperty.Defaults.lavalogged().apply(state, true), 3);
                world.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(world));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    default ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState state) {
        if (InfProperty.Defaults.lavalogged().fetch(state)) {
            world.setBlock(pos, InfProperty.Defaults.lavalogged().apply(state, false), 3);
            return new ItemStack(Items.LAVA_BUCKET);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    default Optional<SoundEvent> getPickupSound() {
        return Fluids.LAVA.getPickupSound();
    }
}
