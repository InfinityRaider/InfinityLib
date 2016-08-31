package com.infinityraider.infinitylib.render.block;

import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface IBlockRenderingHandler<B extends BlockBase & ICustomRenderedBlock> {
    /**
     * Gets the block tied to this renderer, used for registering this renderer.
     * A pointer to the Block is saved and referenced.
     *
     * @return the block for this renderer
     */
    B getBlock();

    /**
     * Returns a list containing a ResourceLocation for every texture used to render this Block.
     * Passed textures are stitched to the Minecraft texture map and icons can be retrieved from them.
     * @return a list of ResourceLocations
     */
    List<ResourceLocation> getAllTextures();

    /**
     * Called to render the block at a specific place in the world,
     * startDrawing() has already been called on the tessellator object.
     * The tessellator is also translated to the block's position
     *
     * @param tessellator tessellator object to draw quads
     * @param world the world for the block
     * @param pos the position for the block
     * @param x the precise x-position of the block (only relevant for TESR calls)
     * @param y the precise y-position of the block (only relevant for TESR calls)
     * @param z the precise z-position of the block (only relevant for TESR calls)
     * @param state the state of the block
     * @param block the block
     */
    void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, B block);

    /**
     * Called to render the block in an inventory
     * startDrawing() has already been called on the tessellator object.
     *
     * @param tessellator tessellator object to draw quads
     * @param world the world object
     * @param state the state of the block
     * @param block the block
     * @param stack stack containing this block as an item
     * @param entity entity holding the stack
     * @param type camera transform type
     */
    void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, B block,
                              ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type);

    /**
     * Gets the main icon used for this renderer, used for the particle
     * @return the particle icon
     */
    TextureAtlasSprite getIcon();

    /**
     * @return true if ambient occlusion should be applied when rendering this block
     */
    boolean applyAmbientOcclusion();

    /**
     * Checks if this should have 3D rendering in inventories
     * @return true to have 3D inventory rendering
     */
    boolean doInventoryRendering();
}
