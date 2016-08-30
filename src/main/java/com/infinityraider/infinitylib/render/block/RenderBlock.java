package com.infinityraider.infinitylib.render.block;

import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.render.item.IItemRenderingHandler;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public abstract class RenderBlock<B extends BlockBase & ICustomRenderedBlock> implements IItemRenderingHandler {

	private final B block;
	private final boolean renderInventory;
	private final boolean renderStatic;

	public RenderBlock(B block, boolean renderInventory, boolean renderStatic) {
		this.block = block;
		this.renderInventory = renderInventory;
		this.renderStatic = renderStatic;
	}
	
	/**
	 * Gets the block tied to this renderer, used for registering this renderer.
	 * A pointer to the Block is saved and referenced.
	 *
	 * @return the block for this renderer
	 */
	public final B getBlock() {
		return block;
	}

	public void renderStatic(ITessellator tess, IBlockAccess world, IBlockState state, BlockPos pos) {
	}

	@Override
	public void renderItem(ITessellator tessellator, World world, ItemStack stack, EntityLivingBase entity) {
		// NOPE...
	}

	/**
	 * Gets the main icon used for this renderer, used for the particle
	 *
	 * @return the particle icon
	 */
	public abstract TextureAtlasSprite getIcon();

	/**
	 * @return true if ambient occlusion should be applied when rendering this
	 * block
	 */
	public abstract boolean applyAmbientOcclusion();

	/**
	 * Checks if this should have 3D rendering in inventories
	 *
	 * @return true to have 3D inventory rendering
	 */
	public final boolean hasInventoryRendering() {
		return this.renderInventory;
	}

	/**
	 * Return true from here to have this renderer have static behaviour,
	 * meaning the vertex buffer is only reloaded on a chunk update.
	 *
	 * @return true if this renderer has static dynamic rendering behaviour
	 */
	public final boolean hasStaticRendering() {
		return this.renderStatic;
	}

	@Override
	public List<ResourceLocation> getAllTextures() {
		return this.block.getTextures();
	}

}
