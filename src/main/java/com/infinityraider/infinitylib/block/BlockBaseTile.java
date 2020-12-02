package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.multiblock.IMultiBlockComponent;
import com.infinityraider.infinitylib.block.tile.IRotatableTile;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockBaseTile<T extends TileEntityBase> extends BlockBase implements IInfinityBlockWithTile<T> {

    public BlockBaseTile(String name, Properties properties) {
        super(name, properties);
    }

    /**
     * Sets the block's orientation based upon the direction the player is
     * looking when the block is placed.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityBase) {
            TileEntityBase tile = (TileEntityBase) world.getTileEntity(pos);
            if(tile instanceof IRotatableTile) {
                Direction dir = entity.getHorizontalFacing();
                ((IRotatableTile) tile).setOrientation(dir.getOpposite());
            }
            if((tile instanceof IMultiBlockComponent) && !world.isRemote) {
                IMultiBlockComponent component = (IMultiBlockComponent) tile;
                component.getMultiBlockManager().onBlockPlaced(world, pos, component);
            }
        }
    }

    @Override
    public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int data) {
        super.eventReceived(state, world, pos, id, data);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, data);
    }
}
