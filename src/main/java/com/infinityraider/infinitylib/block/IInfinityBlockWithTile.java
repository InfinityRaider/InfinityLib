package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IInfinityBlockWithTile<T extends TileEntity> extends IInfinityBlock {
    TileEntityBase createTileEntity();
}
