package com.infinityraider.infinitylib.render.block;

import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderBlockBase<B extends BlockBase & ICustomRenderedBlock> implements IBlockRenderingHandler<B> {
    private final B block;
    private final boolean inv;

    protected RenderBlockBase(B block, boolean inv) {
        this.block = block;
        this.inv = inv;
    }

    @Override
    public B getBlock() {
        return block;
    }

    @Override
    public boolean doInventoryRendering() {
        return inv;
    }
}
