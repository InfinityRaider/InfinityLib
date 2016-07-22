package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.render.block.IBlockRenderingHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Implemented in a Block class to have special rendering handling for the block
 */
public interface ICustomRenderedBlock<T extends TileEntityBase> {
    /**
     * Gets called to create the IBlockRenderingHandler instance to render this block with
     * @return a new IBlockRenderingHandler object for this block
     */
    @SideOnly(Side.CLIENT)
    IBlockRenderingHandler getRenderer();

    /**
     * Gets an array of ResourceLocations used for the model of this block, all block states for this block will use this as key in the model registry
     * @return a unique ModelResourceLocation for this block
     */
    @SideOnly(Side.CLIENT)
    ModelResourceLocation getBlockModelResourceLocation();

    @SideOnly(Side.CLIENT)
    boolean needsRenderUpdate(IBlockAccess world, BlockPos pos, IBlockState state, T tile);
}
