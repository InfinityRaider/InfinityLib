package com.infinityraider.infinitylib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public interface IInfinityBlockWithTile<T extends BlockEntity> extends IInfinityBlock, IForgeBlock, EntityBlock {
    @Override
    @Nullable
    default T newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return this.getTileEntityFactory().apply(pos, state);
    }

    BiFunction<BlockPos, BlockState, T> getTileEntityFactory();
}
