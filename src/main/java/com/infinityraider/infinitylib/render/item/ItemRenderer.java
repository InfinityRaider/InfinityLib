package com.infinityraider.infinitylib.render.item;

import java.util.Collection;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Set;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer implements IModelGeometry<ItemRenderer> {

    private final IItemRenderingHandler renderer;

    public ItemRenderer(IItemRenderingHandler renderer) {
        this.renderer = renderer;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        return new BakedInfItemModel(this.renderer.getVertexFormat(), renderer, spriteGetter);
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return this.renderer.getAllTextures();
    }
}
