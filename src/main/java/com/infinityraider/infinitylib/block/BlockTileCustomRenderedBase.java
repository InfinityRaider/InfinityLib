package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.tile.TileEntityBase;

@SuppressWarnings("unused")
public abstract class BlockTileCustomRenderedBase<T extends TileEntityBase> extends BlockBaseTile<T> implements ICustomRenderedBlockWithTile<T> {

	public BlockTileCustomRenderedBase(String name, Properties properties) {
		super(name, properties);
	}

}
