package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.render.item.IItemRenderingHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public interface ICustomRenderedItem extends IInfinityItem {
    /**
     * Gets called to create the IBlockRenderingHandler instance to render this block with
     * @return a new IItemRenderingHandler object for this block
     */
    @OnlyIn(Dist.CLIENT)
    IItemRenderingHandler getRenderer();
}
