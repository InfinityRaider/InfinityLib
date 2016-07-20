package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.blockstate.BlockStateSpecial;
import com.infinityraider.infinitylib.block.blockstate.IBlockStateSpecial;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

@SuppressWarnings("unused")
public abstract class BlockTileCustomRenderedBase<T extends TileEntityBase> extends BlockBaseTile<T> implements ICustomREnderedBlockWithTile<T> {
    public BlockTileCustomRenderedBase(String name, Material blockMaterial) {
        super(name, blockMaterial);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final IBlockStateSpecial<T, ? extends IBlockState> getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new BlockStateSpecial<>(state, pos, (T) world.getTileEntity(pos));
    }
}
