package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockBaseTile<T extends TileEntityBase> extends BlockBase implements IInfinityBlockWithTile<T> {

    public BlockBaseTile(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int id, int data) {
        super.triggerEvent(state, world, pos, id, data);
        BlockEntity tile = world.getBlockEntity(pos);
        return tile != null && tile.triggerEvent(id, data);
    }
}
