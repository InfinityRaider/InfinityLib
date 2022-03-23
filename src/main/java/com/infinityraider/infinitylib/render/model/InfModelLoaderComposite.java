package com.infinityraider.infinitylib.render.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.reference.Constants;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;


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
public class InfModelLoaderComposite implements InfModelLoader<InfModelLoaderComposite.Geometry> {
    private static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "composite");

    private static final InfModelLoaderComposite INSTANCE = new InfModelLoaderComposite();

    public static InfModelLoaderComposite getInstance() {
        return INSTANCE;
    }

    private InfModelLoaderComposite() {}

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
    }

    @Nonnull
    @Override
    public Geometry read(@Nonnull JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        // Check if any parts are defined
        if (!modelContents.has("parts")) {
            throw new RuntimeException("Composite model requires a \"parts\" element.");
        }
        // Fetch transformations
        Optional<JsonObject> transformations = modelContents.has("transformations") ?
                Optional.of(modelContents.getAsJsonObject("transformations")) :
                Optional.empty();
        // Parse parts
        ImmutableMap.Builder<String, Submodel> parts = ImmutableMap.builder();
        for (Map.Entry<String, JsonElement> part : modelContents.get("parts").getAsJsonObject().entrySet()) {
            // This is where we modify the Forge code to allow custom transformations
            ModelState modelTransform = transformations
                    .filter(json -> json.has(part.getKey()))
                    .map(json -> readTransformation(json.getAsJsonObject(part.getKey())))
                    .orElse(SimpleModelState.IDENTITY);
            parts.put(part.getKey(), new Submodel(
                    part.getKey(),
                    deserializationContext.deserialize(part.getValue(), BlockModel.class),
                    modelTransform
            ));
        }
        ImmutableMap<String, Submodel> partsMap = parts.build();
        String particleParent = null;
        // Check for parent particle
        if(modelContents.has("particle")) {
            particleParent = modelContents.get("particle").getAsString();
            if(!partsMap.containsKey(particleParent)) {
                throw new RuntimeException("Invalid particle inheritance in composite model: \"" + particleParent + "\"");
            }
        }
        return new Geometry(parts.build(), particleParent);
    }

    private static ModelState readTransformation(JsonObject json) {
        // rotation
        Vector3f rotation = parseVector(json, "rotation", ItemTransform.Deserializer.DEFAULT_ROTATION);
        Matrix4f matrix = new Matrix4f(new Quaternion(rotation.x(), rotation.y(), rotation.z(), true));
        // translation
        Vector3f translation = parseVector(json, "translation", ItemTransform.Deserializer.DEFAULT_TRANSLATION);
        translation.mul(Constants.UNIT);
        matrix.multiply(Matrix4f.createTranslateMatrix(translation.x(), translation.y(), translation.z()));
        // scale
        Vector3f scale = parseVector(json, "scale", ItemTransform.Deserializer.DEFAULT_SCALE);
        matrix.multiply(Matrix4f.createScaleMatrix(scale.x(), scale.y(), scale.z()));
        Transformation transform = new Transformation(matrix);
        return new ModelState() {
            @Nonnull
            @Override
            public Transformation getRotation() {
                return transform;
            }
        };
    }

    private static Vector3f parseVector(JsonObject json, String key, Vector3f fallback) {
        if (!json.has(key)) {
            return fallback;
        } else {
            JsonArray jsonarray = GsonHelper.getAsJsonArray(json, key);
            if (jsonarray.size() != 3) {
                throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonarray.size());
            } else {
                float[] afloat = new float[3];
                for (int i = 0; i < afloat.length; ++i) {
                    afloat[i] = GsonHelper.convertToFloat(jsonarray.get(i), key + "[" + i + "]");
                }
                return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
        }
    }

    //Shamelessly copied from Forge as no changes are needed here
    public static class Geometry implements IMultipartModelGeometry<Geometry> {
        private final ImmutableMap<String, Submodel> parts;
        private final String particlePart;

        Geometry(ImmutableMap<String, Submodel> parts, @Nullable String particlePart) {
            this.parts = parts;
            this.particlePart = particlePart;
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
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter,
                               ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {

            Material particleLocation = owner.resolveTexture("particle");
            TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

            ImmutableMap.Builder<String, BakedModel> bakedParts = ImmutableMap.builder();
            for (Map.Entry<String, Submodel> part : parts.entrySet()) {
                Submodel submodel = part.getValue();
                if (!owner.getPartVisibility(submodel)) {
                    continue;
                }
                bakedParts.put(part.getKey(), submodel.bakeModel(bakery, spriteGetter, modelTransform, modelLocation));
            }

            return new BakedSubModel(owner.isShadedInGui(), owner.isSideLit(), owner.useSmoothLighting(), particle,
                    bakedParts.build(), owner.getCombinedTransform(), overrides, this.particlePart);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter,
                                                      Set<Pair<String, String>> missingTextureErrors) {

            Set<Material> textures = new HashSet<>();
            for (Submodel part : parts.values()) {
                textures.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
            }
            return textures;
        }
    }

    // Copied from Forge with a hack-y fix for extra data not being correctly loaded when embedded in a multipart model
    private static class BakedSubModel extends CompositeModel {
        private final ImmutableMap<String, BakedModel> bakedParts;
        private final String particleParent;

        public BakedSubModel(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle,
                             ImmutableMap<String, BakedModel> bakedParts, ModelState combinedTransform,
                             ItemOverrides overrides, @Nullable String particleParent) {
            super(isGui3d, isSideLit, isAmbientOcclusion, particle, bakedParts, combinedTransform, overrides);
            this.bakedParts = bakedParts;
            this.particleParent = particleParent;
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
            if(extraData instanceof CompositeModelData) {
                return super.getQuads(state, side, rand, extraData);
            } else {
                List<BakedQuad> quads = new ArrayList<>();
                for(Map.Entry<String, BakedModel> entry : bakedParts.entrySet()) {
                    quads.addAll(entry.getValue().getQuads(state, side, rand, extraData));
                }
                return quads;
            }
        }

        @Nonnull
        @Override
        @SuppressWarnings("deprecation")
        public TextureAtlasSprite getParticleIcon() {
            if(this.particleParent == null) {
                return super.getParticleIcon();
            }
            return this.bakedParts.get(this.particleParent).getParticleIcon();
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleIcon(@Nonnull IModelData extraData) {
            if(this.particleParent == null) {
                return super.getParticleIcon(extraData);
            }
            return this.bakedParts.get(this.particleParent).getParticleIcon(extraData);
        }
    }

    //Copied from Forge, albeit with modifications in the bakeModel method
    private static class Submodel implements IModelGeometryPart {
        private final String name;
        private final BlockModel model;
        private final ModelState modelTransform;

        private Submodel(String name, BlockModel model, ModelState modelTransform) {
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
                             Function<Material, TextureAtlasSprite> spriteGetter,
                             ModelState modelTransform, ResourceLocation modelLocation) {

            throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
        }

        public BakedModel bakeModel(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter,
                                     ModelState modelTransform, ResourceLocation modelLocation) {
            BakedModel baked;
            //Discern between Forge and Vanilla models, Forge handles the transformation fine, however, Vanilla does not
            if (this.model.customData.getCustomGeometry() == null) {
                //Vanilla: use the identity transformation, but intercept baked quads and transform them right after baking
                TransformingFaceBakery.getInstance().pushQuadTransform(this.modelTransform.getRotation());
                baked = model.bake(bakery, model, spriteGetter, new CompositeModelState(SimpleModelState.IDENTITY, modelTransform,
                        this.modelTransform.isUvLocked() || modelTransform.isUvLocked()), modelLocation, true);
                TransformingFaceBakery.getInstance().popQuadTransform();
            } else {
                //Forge: carry on with the predefined transformation
                baked = model.bake(bakery, model, spriteGetter, new CompositeModelState(this.modelTransform, modelTransform,
                        this.modelTransform.isUvLocked() || modelTransform.isUvLocked()), modelLocation, true);
            }
            return baked;
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter,
                                                      Set<Pair<String, String>> missingTextureErrors) {
            return model.getMaterials(modelGetter, missingTextureErrors);
        }
    }
}
