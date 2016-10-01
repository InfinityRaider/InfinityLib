package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.blockstate.IBlockStateWithPos;
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
public interface ICustomRenderedBlock extends IInfinityBlock {
    /**
     * This is here to make sure a block state containing the tile entity and block position of the block are passed in the block's getExtendedState method
     * @param state the block's in world state (can be an IExtendedState)
     * @param world the world
     * @param pos the block's position in the world
     * @return a special block state containing the tile entity and the position
     */
    @SuppressWarnings("unused")
    IBlockStateWithPos<? extends IBlockState> getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos);

    /**
     * Gets called to create the IBlockRenderingHandler instance to render this
     * block with
     *
     * @return a new IBlockRenderingHandler object for this block
     */
    @SideOnly(Side.CLIENT)
    IBlockRenderingHandler getRenderer();

    /**
     * Gets an array of ResourceLocations used for the model of this block, all
     * block states for this block will use this as key in the model registry
     *
     * @return a unique ModelResourceLocation for this block
     */
    @SideOnly(Side.CLIENT)
    default ModelResourceLocation getBlockModelResourceLocation() {
        return new ModelResourceLocation(this.getRegistryName() + "");
    }
}
