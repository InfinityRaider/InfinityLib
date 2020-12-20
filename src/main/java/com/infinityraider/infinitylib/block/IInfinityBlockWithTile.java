package com.infinityraider.infinitylib.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlock;

import java.util.function.BiFunction;

public interface IInfinityBlockWithTile<T extends TileEntity> extends IInfinityBlock, IForgeBlock, ITileEntityProvider {
    @Override
    default T createNewTileEntity(IBlockReader world) {
        return this.getTileEntityFactory().apply(this.cast().getDefaultState(), world);
    }

    @Override
    default boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    default T createTileEntity(BlockState state, IBlockReader world) {
        return this.getTileEntityFactory().apply(state, world);
    }

    BiFunction<BlockState, IBlockReader, T> getTileEntityFactory();
}
