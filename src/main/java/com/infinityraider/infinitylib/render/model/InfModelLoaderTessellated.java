package com.infinityraider.infinitylib.render.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.infinityraider.infinitylib.InfinityLib;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class InfModelLoaderTessellated implements InfModelLoader<InfModelLoaderTessellated.TessellatedGeometry> {
    public static final InfModelLoaderTessellated INSTANCE = new InfModelLoaderTessellated();

    private static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "tessellated");

    private InfModelLoaderTessellated() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {}

    @Override
    public TessellatedGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return null;
    }

    public static class TessellatedGeometry implements IModelGeometry<TessellatedGeometry> {

        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
            return null;
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            return null;
        }
    }
}
