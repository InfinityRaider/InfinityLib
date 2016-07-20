package com.infinityraider.infinitylib.block.blockstate;

import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

/**
 * Special block state containing the tile entity and block position of a block
 * @param <T> Tile Entity type
 */
public interface IBlockStateSpecial<T extends TileEntityBase, S extends IBlockState> extends IBlockState {
    T getTileEntity();

    BlockPos getPos();

    /**
     * Gets the original block state wrapped in this block state
     * @return original state
     */
    S getWrappedState();
}
