package com.infinityraider.infinitylib.block.blockstate;

import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Special block state to pass the tile entity and position of a block to the renderer
 * @param <T> TileEntity type
 * @param <S> The original block state
 */
public class BlockStateSpecial<T extends TileEntityBase, S extends IBlockState> extends BlockStateContainer.StateImplementation
        implements IBlockStateSpecial<T, S> {

    private final T tile;
    private final BlockPos pos;
    private final S state;

    public BlockStateSpecial(S state, BlockPos pos, T tile) {
        super(state.getBlock(), state.getProperties());
        this.state = state;
        this.tile = tile;
        this.pos = pos;
    }

    /**
     * @return Return the TileEntity for this block state
     */
    @Override
    public T getTileEntity() {
        return tile;
    }

    /**
     * @return The BlockPOs for this block state
     */
    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public S getWrappedState() {
        return this.state;
    }
}
