package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.render.item.IItemRenderingHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public interface ICustomRenderedItem<I extends Item> extends IInfinityItem {
    /**
     * Gets called to create the IBlockRenderingHandler instance to render this block with
     * @return a new IItemRenderingHandler object for this block
     */
    @SideOnly(Side.CLIENT)
    IItemRenderingHandler<I> getRenderer();

    /**
     * Gets a list of all textures necessary to render this item,
     * returned textures will be stitched to the atlas
     * @return list of textures
     */
    @SideOnly(Side.CLIENT)
    List<ResourceLocation> getTextures();

    /**
     * Gets an array of ResourceLocations used for the model of this block, all block states for this block will use this as key in the model registry
     * @return a unique ModelResourceLocation for this block
     */
    @SideOnly(Side.CLIENT)
    ModelResourceLocation getItemModelResourceLocation();
}
