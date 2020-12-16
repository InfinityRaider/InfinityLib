package com.infinityraider.infinitylib.render.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.infinityraider.infinitylib.InfinityLib;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

import java.util.*;
import java.util.function.Function;

import static net.minecraft.client.renderer.model.ItemTransformVec3f.Deserializer.*;

/**
 * Composite model which allows transformations of the different sub-models
 * This class is mostly copied from Forge's net.minecraftforge.client.model.CompositeModel,
 * with the exception of the modification on lines 171 and 253, which uses the IDENTITY transformation for each of the sub-models
 *
 * Theoretically it could be as easy as just extending the Loader class, overriding the read() method, reading the
 * IModelTransform object, and pass it on the pre-existing logic.
 * However, Vanilla does not want to play ball with arbitrary transformations, therefore we have to
 * hijack Vanilla's FaceBakery, and manually transform each of the BakedQuads for Vanilla sub-models.
 *
 * Once Forge decides to implement transformations for the different sub-models, this class becomes redundant.
 */
@OnlyIn(Dist.CLIENT)
public class InfCompositeModelLoader implements InfModelLoader<InfCompositeModelLoader.Geometry> {
    public static final InfCompositeModelLoader INSTANCE = new InfCompositeModelLoader();

    private static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "composite");

    private InfCompositeModelLoader() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
    }

    @Override
    public Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        if (!modelContents.has("parts"))
            throw new RuntimeException("Composite model requires a \"parts\" element.");
        Optional<JsonObject> transformations = modelContents.has("transformations") ?
                Optional.of(modelContents.getAsJsonObject("transformations")) :
                Optional.empty();
        ImmutableMap.Builder<String, Submodel> parts = ImmutableMap.builder();
        for (Map.Entry<String, JsonElement> part : modelContents.get("parts").getAsJsonObject().entrySet()) {
            // This is where we modify the Forge code to allow custom transformations
            IModelTransform modelTransform = transformations
                    .filter(json -> json.has(part.getKey()))
                    .map(json -> readTransformation(json.getAsJsonObject(part.getKey())))
                    .orElse(SimpleModelTransform.IDENTITY);
            parts.put(part.getKey(), new Submodel(
                    part.getKey(),
                    deserializationContext.deserialize(part.getValue(), BlockModel.class),
                    modelTransform
            ));
        }
        return new Geometry(parts.build());
    }

    private static IModelTransform readTransformation(JsonObject json) {
        // rotation
        Vector3f rotation = parseVector(json, "rotation", ROTATION_DEFAULT);
        Matrix4f matrix = new Matrix4f(new Quaternion(rotation.getX(), rotation.getY(), rotation.getZ(), true));
        // translation
        Vector3f translation = parseVector(json, "translation", TRANSLATION_DEFAULT);
        translation.mul(0.0625F);
        matrix.mul(Matrix4f.makeTranslate(translation.getX(), translation.getY(), translation.getZ()));
        // scale
        Vector3f scale = parseVector(json, "scale", SCALE_DEFAULT);
        matrix.mul(Matrix4f.makeScale(scale.getX(), scale.getY(), scale.getZ()));
        TransformationMatrix transform = new TransformationMatrix(matrix);
        return new IModelTransform() {
            @Override
            public TransformationMatrix getRotation() {
                return transform;
            }
        };
    }

    private static Vector3f parseVector(JsonObject json, String key, Vector3f fallback) {
        if (!json.has(key)) {
            return fallback;
        } else {
            JsonArray jsonarray = JSONUtils.getJsonArray(json, key);
            if (jsonarray.size() != 3) {
                throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonarray.size());
            } else {
                float[] afloat = new float[3];
                for (int i = 0; i < afloat.length; ++i) {
                    afloat[i] = JSONUtils.getFloat(jsonarray.get(i), key + "[" + i + "]");
                }
                return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
        }
    }

    //Shamelessly copied from Forge as no changes are needed here
    public static class Geometry implements IMultipartModelGeometry<Geometry> {
        private final ImmutableMap<String, Submodel> parts;

        Geometry(ImmutableMap<String, Submodel> parts) {
            this.parts = parts;
        }

        @Override
        public Collection<? extends IModelGeometryPart> getParts() {
            return parts.values();
        }

        @Override
        public Optional<? extends IModelGeometryPart> getPart(String name) {
            return Optional.ofNullable(parts.get(name));
        }

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                                IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {

            RenderMaterial particleLocation = owner.resolveTexture("particle");
            TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

            ImmutableMap.Builder<String, IBakedModel> bakedParts = ImmutableMap.builder();
            for (Map.Entry<String, Submodel> part : parts.entrySet()) {
                Submodel submodel = part.getValue();
                if (!owner.getPartVisibility(submodel)) {
                    continue;
                }
                bakedParts.put(part.getKey(), submodel.bakeModel(bakery, spriteGetter, modelTransform, modelLocation));
            }

            return new CompositeModel(owner.isShadedInGui(), owner.isSideLit(), owner.useSmoothLighting(), particle,
                    bakedParts.build(), owner.getCombinedTransform(), overrides);
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter,
                                                      Set<Pair<String, String>> missingTextureErrors) {

            Set<RenderMaterial> textures = new HashSet<>();
            for (Submodel part : parts.values()) {
                textures.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
            }
            return textures;
        }
    }

    //Copied from Forge, albeit with modifications in the bakeModel method
    private static class Submodel implements IModelGeometryPart {
        private final String name;
        private final BlockModel model;
        private final IModelTransform modelTransform;

        private Submodel(String name, BlockModel model, IModelTransform modelTransform) {
            this.name = name;
            this.model = model;
            this.modelTransform = modelTransform;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
                             Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                             IModelTransform modelTransform, ResourceLocation modelLocation) {

            throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
        }

        public IBakedModel bakeModel(ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                                     IModelTransform modelTransform, ResourceLocation modelLocation) {
            IBakedModel baked;
            //Discern between Forge and Vanilla models, Forge handles the transformation fine, however, Vanilla does not
            if (this.model.customData.getCustomGeometry() == null) {
                //Vanilla: use the identity transformation, but intercept baked quads and transform them right after baking
                InfFaceBakery.getInstance().startTransformingQuads(this.modelTransform.getRotation());
                baked = model.bakeModel(bakery, spriteGetter, new ModelTransformComposition(SimpleModelTransform.IDENTITY, modelTransform,
                        this.modelTransform.isUvLock() || modelTransform.isUvLock()), modelLocation);
                InfFaceBakery.getInstance().stopTransformingQuads();
            } else {
                //Forge: carry on with the predefined transformation
                baked = model.bakeModel(bakery, spriteGetter, new ModelTransformComposition(this.modelTransform, modelTransform,
                        this.modelTransform.isUvLock() || modelTransform.isUvLock()), modelLocation);
            }
            return baked;
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter,
                                                      Set<Pair<String, String>> missingTextureErrors) {
            return model.getTextures(modelGetter, missingTextureErrors);
        }
    }

}
