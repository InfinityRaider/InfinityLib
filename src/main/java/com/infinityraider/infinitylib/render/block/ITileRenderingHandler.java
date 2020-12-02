package com.infinityraider.infinitylib.render.block;

import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlockWithTile;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface ITileRenderingHandler<B extends BlockBase & ICustomRenderedBlockWithTile<T>, T extends TileEntityBase> extends IBlockRenderingHandler<B> {
    /**
     * Gets the TileEntity for this renderer (this should be a new TileEntity which is not physically in a World),
     * it is used for registering this renderer and inventory rendering.
     * The class from this TileEntity and the object passed are saved and referenced,
     * The TileEntity is passed to this renderer for inventory rendering, it is not in a world so you can directly change fields to render it
     * This method may return null if there is no tile entity
     *
     * @return a new TileEntity for this renderer
     */
    T getTileEntity();

    /**
     * Returns a list containing a ResourceLocation for every texture used to render this Block.
     * Passed textures are stitched to the Minecraft texture map and icons can be retrieved from them.
     *
     * @return a list of ResourceLocations
     */
    List<RenderMaterial> getAllTextures();

    /**
     * Called to render the block at a specific place in the world,
     * startDrawing() has already been called on the tessellator object.
     * The tessellator is also translated to the block's position
     * @param tessellator   tessellator object to draw quads
     * @param world         the world for the block
     * @param pos           the position for the block
     * @param block         the block
     * @param tile          the tile entity (can be null if there is no tile entity)
     * @param partialTick   partial tick, only useful for dynamic rendering
     */
    void renderWorldBlockDynamic(ITessellator tessellator, World world, BlockPos pos,
                                 B block, T tile, float partialTick);

    /**
     * This method is to used by ITileRenderingHandlers.
     * This method has been retrofitted.
     * This method is the preferred method of inventory rendering.
     */
    @Override
    default void renderInventoryBlock(ITessellator tessellator, World world, BlockState state, B block,
                                      ItemStack stack, LivingEntity entity, ItemCameraTransforms.TransformType type) {
        renderInventoryBlock(tessellator, world, state, block, getTileEntity(), stack, entity, type);
    }

    /**
     * Called to render the block in an inventory
     * startDrawing() has already been called on the tessellator object.
     *
     * @param tessellator tessellator object to draw quads
     * @param world       the world object
     * @param state       the state of the block
     * @param block       the block
     * @param tile        the tile entity passed from getTileEntity() (can be null if there is no tile entity)
     * @param stack       stack containing this block as an item
     * @param entity      entity holding the stack
     * @param type        camera transform type
     */
    void renderInventoryBlock(ITessellator tessellator, World world, BlockState state, B block,
                              T tile, ItemStack stack, LivingEntity entity, ItemCameraTransforms.TransformType type);

    /**
     * Gets the main icon used for this renderer, used for the particle
     *
     * @return the particle icon
     */
    TextureAtlasSprite getIcon();

    /**
     * @return true if ambient occlusion should be applied when rendering this block
     */
    boolean applyAmbientOcclusion();

    /**
     * Checks if this should have 3D rendering in inventories
     *
     * @return true to have 3D inventory rendering
     */
    boolean doInventoryRendering();

    /**
     * Return true from here to have this renderer have dynamic behaviour,
     * meaning the vertex buffer is reloaded every render tick (TESR behaviour).
     * If the renderer has dynamic behaviour, getTileEntity() should not return null.
     *
     * @return if this renderer has dynamic rendering behaviour
     */
    boolean hasDynamicRendering();

    /**
     * Return true from here to have this renderer have static behaviour,
     * meaning the vertex buffer is only reloaded on a chunk update.
     *
     * @return true if this renderer has static dynamic rendering behaviour
     */
    boolean hasStaticRendering();
}
