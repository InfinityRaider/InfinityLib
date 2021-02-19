package com.infinityraider.infinitylib.render.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.infinityraider.infinitylib.InfinityLib;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class ModelLoaderDynamicTexture implements InfModelLoader<ModelLoaderDynamicTexture.Geometry> {
    private static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "dynamic_texture");

    private static final ModelLoaderDynamicTexture INSTANCE = new ModelLoaderDynamicTexture();

    public static ModelLoaderDynamicTexture getInstance() {
        return INSTANCE;
    }

    private ModelLoaderDynamicTexture() {}

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        // TODO
        return null;
    }

    public static class Geometry implements IModelGeometry<Geometry> {

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
            return null;
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            return Collections.emptyList();
        }
    }

    public static class DynamicTextureModel implements IBakedModel {

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
            return null;
        }

        @Override
        public boolean isAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean isSideLit() {
            return false;
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
        public ItemOverrideList getOverrides() {
            return null;
        }
    }
}
