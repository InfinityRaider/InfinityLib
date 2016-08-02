package com.infinityraider.infinitylib.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import com.infinityraider.infinitylib.render.block.RenderBlock;

/**
 * Implemented in a Block class to have special rendering handling for the block
 */
public interface ICustomRenderedBlock {
    /**
     * Gets called to create the IBlockRenderingHandler instance to render this block with
     * @return a new IBlockRenderingHandler object for this block
     */
    @SideOnly(Side.CLIENT)
    RenderBlock getRenderer();

    /**
     * Gets an array of ResourceLocations used for the model of this block, all block states for this block will use this as key in the model registry
     * @return a unique ModelResourceLocation for this block
     */
    @SideOnly(Side.CLIENT)
    ModelResourceLocation getBlockModelResourceLocation();

    /**
     * Quads are cached and only recalculated when a render update is necessary. If this block does not have a tile entity, this method will not be called,
     * instead the previous block state is compared with the new block state to determine if a render update is needed.
     * @param world world object
     * @param pos position of the block
     * @param state block state of the block
     * @param tile tile entity at the passed position
     * @return if the appearance of the block has changed and quads need to be redrawn
     */
    @SideOnly(Side.CLIENT)
	default boolean needsRenderUpdate(World world, BlockPos pos, IBlockState state) {
		// TODO: Figure out how to update on blockstate change!
		return true;
		//return !world.getBlockState(pos).equals(state);
	}

    List<ResourceLocation> getTextures();
}
