package com.infinityraider.infinitylib.block.tile;

import net.minecraft.block.state.IBlockState;

public interface ITileEntityStateCache {
    IBlockState getState();

    void resetSate();

    void resetSate(IBlockState newState);
}
