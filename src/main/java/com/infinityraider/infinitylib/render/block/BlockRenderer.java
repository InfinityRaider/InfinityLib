package com.infinityraider.infinitylib.render.block;

import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class BlockRenderer<B extends BlockBase & ICustomRenderedBlock> implements IModelGeometry<BlockRenderer<B>> {

    private final B block;
    private final IBlockRenderingHandler<B> renderer;

    public BlockRenderer(IBlockRenderingHandler<B> renderer) {
        this.block = renderer.getBlock();
        this.renderer = renderer;
    }

    public B getBlock() {
        return block;
    }

    public IBlockRenderingHandler<B> getRenderer() {
        return renderer;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        return new BakedInfBlockModel<>(block, this.renderer.getVertexFormat(), renderer, spriteGetter, renderer.doInventoryRendering());
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return renderer.getAllTextures();
    }
}
