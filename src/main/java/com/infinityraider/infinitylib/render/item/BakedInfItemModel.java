/*
 */
package com.infinityraider.infinitylib.render.item;

import com.google.common.base.Function;
import java.util.List;
import javax.vecmath.Matrix4f;

import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author RlonRyan
 */
@SideOnly(Side.CLIENT)
public class BakedInfItemModel implements IPerspectiveAwareModel {

	private final BakedInfItemSuperModel parent;
	private final ItemStack stack;
	private final World world;
	private final EntityLivingBase entity;

	public BakedInfItemModel(BakedInfItemSuperModel parent, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textures, World world, ItemStack stack, EntityLivingBase entity, IItemRenderingHandler renderer) {
		this.parent = parent;
		this.world = world;
		this.stack = stack;
		this.entity = entity;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		List<BakedQuad> list;

		final TessellatorBakedQuad tessellator = TessellatorBakedQuad.getInstance();
		tessellator.setCurrentFace(side);
		tessellator.setTextureFunction(this.parent.textures);
		tessellator.startDrawingQuads(this.parent.format);
		this.parent.renderer.renderItem(tessellator, world, stack, entity);
		list = tessellator.getQuads();
		tessellator.draw();

		return list;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return null;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type) {
		return Pair.of(this, this.parent.handlePerspective(type));
	}

}
