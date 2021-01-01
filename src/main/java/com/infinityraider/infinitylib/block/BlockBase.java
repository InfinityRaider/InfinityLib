package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class BlockBase extends Block implements IInfinityBlock {
    private final String internalName;

    public BlockBase(String name, Properties properties) {
        super(properties);
        this.internalName = name;
        this.setDefaultState(this.getPropertyConfiguration().defineDefault(this.getStateContainer().getBaseState()));
    }

    @Override
    protected final void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        this.getPropertyConfiguration().fillStateContainer(builder);
    }

    protected abstract InfPropertyConfiguration getPropertyConfiguration();

    @Override
    @Deprecated
    public final BlockState rotate(BlockState state, Rotation rot) {
        return this.getPropertyConfiguration().handleRotation(state, rot);
    }

    @Override
    @Deprecated
    public final BlockState mirror(BlockState state, Mirror mirror) {
        return this.getPropertyConfiguration().handleMirror(state, mirror);
    }

    @Override
    public final FluidState getFluidState(BlockState state) {
        return this.getPropertyConfiguration().isWaterLoggable() && InfProperty.Defaults.waterlogged().fetch(state)
                ? Fluids.WATER.getStillFluidState(false)
                : super.getFluidState(state);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    @Nonnull
    public String getInternalName() {
        return this.internalName;
    }

    public final BlockState waterlog(BlockState state, World world, BlockPos pos) {
        if(this.getPropertyConfiguration().isWaterLoggable()) {
            FluidState fluid = world.getFluidState(pos);
            state = InfProperty.Defaults.waterlogged().apply(state, fluid.getFluid() == Fluids.WATER);
        }
        return state;
    }

    public final void spawnItem(World world, BlockPos pos, ItemStack stack) {
        world.addEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
    }
}
