package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.tile.IInfinityTileEntity;
import com.infinityraider.infinitylib.block.tile.IInfinityTileEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiFunction;

@ParametersAreNonnullByDefault
public interface IInfinityBlockWithTile<T extends BlockEntity & IInfinityTileEntity> extends IInfinityBlock, IForgeBlock, EntityBlock {
    @Override
    @Nullable
    default T newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return this.getTileEntityFactory().apply(pos, state);
    }

    BiFunction<BlockPos, BlockState, T> getTileEntityFactory();

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    default <TE extends BlockEntity> BlockEntityTicker<TE> getTicker(Level level, BlockState state, BlockEntityType<TE> type) {
        if(type instanceof IInfinityTileEntityType) {
            if(((IInfinityTileEntityType) type).isTicking()) {
                return (l, p, s, t) -> {
                    if(t instanceof IInfinityTileEntity) {
                        ((IInfinityTileEntity) t).tick();
                    }
                };
            }
        }
        return EntityBlock.super.getTicker(level, state, type);
    }
}
