package com.infinityraider.infinitylib.render.block;

import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlockWithTile;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.render.RenderUtilBase;

public abstract class RenderBlockWithTileBase<B extends BlockBase & ICustomRenderedBlockWithTile<T>, T extends TileEntityBase> extends RenderUtilBase implements ITileRenderingHandler<B, T> {
    private final B block;
    private final T dummy;
    private final boolean inv;
    private final boolean statRender;
    private final boolean dynRender;

    protected RenderBlockWithTileBase(B block, T te, boolean inv, boolean statRender, boolean dynRender) {
        this.block = block;
        this.dummy = te;
        this.inv = inv;
        this.statRender = statRender;
        this.dynRender = dynRender;
    }

    @Override
    public B getBlock() {
        return block;
    }

    @Override
    public T getTileEntity() {
        return dummy;
    }

    @Override
    public boolean doInventoryRendering() {
        return inv;
    }

    @Override
    public boolean hasDynamicRendering() {
        return dynRender;
    }

    @Override
    public boolean hasStaticRendering() {
        return statRender;
    }
}