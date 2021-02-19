package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockBaseTile<T extends TileEntityBase> extends BlockBase implements IInfinityBlockWithTile<T> {

    public BlockBaseTile(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int data) {
        super.eventReceived(state, world, pos, id, data);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, data);
    }
}
