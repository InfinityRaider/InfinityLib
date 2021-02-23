package com.infinityraider.infinitylib.render.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class InfModelLoaderDynamicTexture implements InfModelLoader<InfModelLoaderDynamicTexture.Geometry> {
    private static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "dynamic_texture");
    private static final String DYNAMIC_TEXTURE = "dynamic";

    private static final InfModelLoaderDynamicTexture INSTANCE = new InfModelLoaderDynamicTexture();

    public static InfModelLoaderDynamicTexture getInstance() {
        return INSTANCE;
    }

    private InfModelLoaderDynamicTexture() {}

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {}

    @Nonnull
    @Override
    public Geometry read(@Nonnull JsonDeserializationContext context, JsonObject modelContents) {
        // Fetch default texture
        if(!modelContents.has("textures")) {
            throw new RuntimeException("Dynamic texture model needs a \"textures\" field.");
        }
        JsonObject textureJson = modelContents.getAsJsonObject("textures");
        if(!textureJson.has("default")) {
            throw new RuntimeException("Dynamic texture model needs a \"default\" texture in the textures.");
        }
        String defaultTexture = textureJson.get("default").getAsString();
        // Fetch model parts
        List<BlockPart> parts = Lists.newArrayList();
        if(modelContents.has("elements")) {
            for(JsonElement jsonelement : JSONUtils.getJsonArray(modelContents, "elements")) {
                parts.add(context.deserialize(jsonelement, BlockPart.class));
            }
        }
        // Return geometry
        return new Geometry(this.flagDynamicTextures(parts), defaultTexture);
    }

    private List<BlockPart> flagDynamicTextures(List<BlockPart> parts) {
        parts.forEach(part -> part.mapFaces.replaceAll((dir, face) -> {
            if(DYNAMIC_TEXTURE.equals(face.texture)) {
                return new DynamicFace(face);
            } else {
                return face;
            }
        }));
        return parts;
    }

    private static class DynamicFace extends BlockPartFace {
        public DynamicFace(BlockPartFace parent) {
            super(parent.cullFace, parent.tintIndex, parent.texture, parent.blockFaceUV);
        }
    }

    public static class Geometry implements IModelGeometry<Geometry>, IRenderUtilities {
        private final List<BlockPart> parts;
        private final String defaultTexture;

        private Geometry(List<BlockPart> parts, String defaultTexture) {
            this.parts = parts;
            this.defaultTexture = defaultTexture;
        }

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                                IModelTransform transform, ItemOverrideList overrides, ResourceLocation modelLocation) {
            return new DynamicTextureModel(this.parts, owner, spriteGetter, transform, overrides, modelLocation, this.defaultTexture);
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter,
                                                      Set<Pair<String, String>> missingTextureErrors) {
            // Create new set
            Set<RenderMaterial> textures = Sets.newHashSet();
            // Add textures for the parts
            for(BlockPart part : this.parts) {
                for(BlockPartFace face : part.mapFaces.values()) {
                    // Skip if it is a dynamic texture
                    if(face instanceof DynamicFace) {
                        continue;
                    }
                    // Add otherwise
                    RenderMaterial texture = owner.resolveTexture(face.texture);
                    if (Objects.equals(texture.getTextureLocation(), MissingTextureSprite.getLocation())) {
                        missingTextureErrors.add(Pair.of(face.texture, owner.getModelName()));
                    }
                    textures.add(texture);
                }
            }
            // Add the default texture
            RenderMaterial texture = this.getRenderMaterial(this.defaultTexture);
            if (Objects.equals(texture.getTextureLocation(), MissingTextureSprite.getLocation())) {
                missingTextureErrors.add(Pair.of(this.defaultTexture, owner.getModelName()));
            }
            textures.add(owner.resolveTexture(this.defaultTexture));
            // Return the textures
            return textures;
        }
    }

    public static class DynamicTextureModel implements IBakedModel, IRenderUtilities {
        private static final ModelProperty<TextureAtlasSprite> PROPERTY_TEXTURE = new ModelProperty<>();

        private final Map<TextureAtlasSprite, IBakedModel> quadCache;

        private final IBakedModel defaultModel;
        private final TextureAtlasSprite defaultSprite;

        private final List<BlockPart> parts;
        private final IModelConfiguration owner;
        private final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;
        private final IModelTransform transform;
        private final ItemOverrideList overrides;
        private final ResourceLocation modelLocation;

        private DynamicTextureModel(List<BlockPart> parts, IModelConfiguration owner,
                                    Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform transform,
                                    ItemOverrideList overrides, ResourceLocation modelLocation, String defaultTexture) {
            this.quadCache = Maps.newConcurrentMap();
            this.parts = parts;
            this.owner = owner;
            this.spriteGetter = spriteGetter;
            this.transform = transform;
            this.overrides = overrides;
            this.modelLocation = modelLocation;
            this.defaultSprite = this.spriteGetter.apply(this.getRenderMaterial(defaultTexture));
            this.defaultModel = this.bakeSubModel(this.getDefaultSprite());
            boolean debug = true;
        }

        public IBakedModel getDefaultModel() {
            return this.defaultModel;
        }

        @Nonnull
        public TextureAtlasSprite getDefaultSprite() {
            return this.defaultSprite;
        }

        @Nonnull
        @SuppressWarnings("deprecation")
        protected TextureAtlasSprite getMaterialSprite(TileEntityDynamicTexture tile) {
            ItemStack materialStack = tile.getMaterial();
            if(materialStack.isEmpty()) {
                return this.getDefaultSprite();
            }
            if(materialStack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) materialStack.getItem()).getBlock();
                return this.getModelForState(block.getDefaultState()).getParticleTexture();
            }
            return this.getDefaultSprite();
        }

        @Nonnull
        @Override
        public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData modelData) {
            TileEntity tile = world.getTileEntity(pos);
            if (!(tile instanceof TileEntityDynamicTexture)) {
                return modelData;
            }
            TileEntityDynamicTexture dynTile = (TileEntityDynamicTexture) tile;
            if (dynTile.getMaterial().isEmpty()) {
                return modelData;
            }
            return new ModelDataMap.Builder().withInitial(PROPERTY_TEXTURE, this.getMaterialSprite((TileEntityDynamicTexture) tile)).build();
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
            return this.getQuads(state, side, rand, this.getDefaultSprite());
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
            if (!extraData.hasProperty(PROPERTY_TEXTURE)) {
                return this.getQuads(state, side, rand);
            }
            TextureAtlasSprite sprite = extraData.getData(PROPERTY_TEXTURE);
            if (sprite == null) {
                return this.getQuads(state, side, rand);

            }
            return this.getQuads(state, side, rand, sprite);
        }

        @Nonnull
        @SuppressWarnings("deprecation")
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull TextureAtlasSprite material) {
            return this.getSubModel(material).getQuads(state, side, rand);
        }

        public IBakedModel getSubModel(@Nonnull TextureAtlasSprite material) {
            if(material.getName().equals(this.getDefaultSprite().getName())) {
                return this.getDefaultModel();
            }
            return this.quadCache.computeIfAbsent(material, this::bakeSubModel);
        }

        protected IBakedModel bakeSubModel(@Nonnull TextureAtlasSprite material) {
            // Create a model builder
            IModelBuilder<?> builder = IModelBuilder.of(owner, overrides, this.getParticleTexture());
            // Iterate over all parts
            for (BlockPart part : this.parts) {
                // Iterate over the faces
                for (Direction direction : part.mapFaces.keySet()) {
                    BlockPartFace face = part.mapFaces.get(direction);
                    // Fetch the necessary texture
                    TextureAtlasSprite sprite;
                    if (face instanceof DynamicFace) {
                        sprite = material;
                    } else {
                        sprite = this.spriteGetter.apply(this.owner.resolveTexture(face.texture));
                    }
                    // Add the quad to the model builder
                    if (face.cullFace == null) {
                        builder.addGeneralQuad(BlockModel.makeBakedQuad(part, face, sprite, direction, this.transform, this.modelLocation));
                    } else {
                        builder.addFaceQuad(
                                this.transform.getRotation().rotateTransform(face.cullFace),
                                BlockModel.makeBakedQuad(part, face, sprite, direction, this.transform, this.modelLocation));
                    }
                }
            }
            // Build the model
            return builder.build();

        }

        @Override
        public boolean isAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean isSideLit() {
            return false;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleTexture() {
            return this.getDefaultSprite();
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleTexture(@Nonnull IModelData extraData) {
            if (!extraData.hasProperty(PROPERTY_TEXTURE)) {
                return this.getParticleTexture();
            }
            TextureAtlasSprite sprite = extraData.getData(PROPERTY_TEXTURE);
            if (sprite == null) {
                return this.getParticleTexture();
            }
            return sprite;
        }

        @Nonnull
        @Override
        public ItemOverrideList getOverrides() {
            return this.overrides;
        }
    }
}
