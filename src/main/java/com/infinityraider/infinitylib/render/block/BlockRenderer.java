package com.infinityraider.infinitylib.render.block;

import com.google.common.base.Function;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@SideOnly(Side.CLIENT)
public class BlockRenderer<B extends BlockBase & ICustomRenderedBlock> implements IModel {

	private final RenderBlock<B> renderer;

	public BlockRenderer(RenderBlock<B> renderer) {
		this.renderer = renderer;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return renderer.getAllTextures();
	}

	@Override
	public BakedInfBlockModel<B> bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new BakedInfBlockModel<>(renderer.getBlock(), format, renderer, bakedTextureGetter, renderer.hasInventoryRendering());
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

}
