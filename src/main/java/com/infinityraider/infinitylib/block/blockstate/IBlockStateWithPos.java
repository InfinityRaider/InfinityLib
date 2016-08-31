package com.infinityraider.infinitylib.block.blockstate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public interface IBlockStateWithPos<S extends IBlockState> extends IBlockState {
    BlockPos getPos();

    /**
     * Gets the original block state wrapped in this block state
     * @return original state
     */
    S getWrappedState();
}
