package com.infinityraider.infinitylib.render.model;

import com.google.common.collect.*;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.data.IModelData;
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
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {}

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
        List<BlockElement> parts = Lists.newArrayList();
        if(modelContents.has("elements")) {
            for(JsonElement jsonelement : GsonHelper.getAsJsonArray(modelContents, "elements")) {
                parts.add(context.deserialize(jsonelement, BlockElement.class));
            }
        }
        // Fetch Item Model transforms
        ItemTransforms itemCameraTransforms = ItemTransforms.NO_TRANSFORMS;
        if (modelContents.has("display")) {
            JsonObject transforms = GsonHelper.getAsJsonObject(modelContents, "display");
            itemCameraTransforms = context.deserialize(transforms, ItemTransforms.class);
        }
        // Return geometry
        return new Geometry(this.flagDynamicTextures(parts), defaultTexture, itemCameraTransforms);
    }

    private List<BlockElement> flagDynamicTextures(List<BlockElement> parts) {
        parts.forEach(part -> part.faces.replaceAll((dir, face) -> {
            if(DYNAMIC_TEXTURE.equals(face.texture)) {
                return new DynamicFace(face);
            } else {
                return face;
            }
        }));
        return parts;
    }

    private static class DynamicFace extends BlockElementFace {
        public DynamicFace(BlockElementFace parent) {
            super(parent.cullForDirection, parent.tintIndex, parent.texture, parent.uv);
        }
    }

    public static class Geometry implements IModelGeometry<Geometry>, IRenderUtilities {
        private final List<BlockElement> parts;
        private final String defaultTexture;
        private final ItemTransforms transforms;

        private Geometry(List<BlockElement> parts, String defaultTexture, ItemTransforms transforms) {
            this.parts = parts;
            this.defaultTexture = defaultTexture;
            this.transforms = transforms;
        }

        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter,
                               ModelState transform, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new DynamicTextureModel(
                    this.parts, owner, this.transforms, spriteGetter, transform, overrides, modelLocation, this.defaultTexture);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter,
                                                      Set<Pair<String, String>> missingTextureErrors) {
            // Create new set
            Set<Material> textures = Sets.newHashSet();
            // Add textures for the parts
            for(BlockElement part : this.parts) {
                for(BlockElementFace face : part.faces.values()) {
                    // Skip if it is a dynamic texture
                    if(face instanceof DynamicFace) {
                        continue;
                    }
                    // Add otherwise
                    Material texture = owner.resolveTexture(face.texture);
                    if (Objects.equals(texture.texture(), MissingTextureAtlasSprite.getLocation())) {
                        missingTextureErrors.add(Pair.of(face.texture, owner.getModelName()));
                    }
                    textures.add(texture);
                }
            }
            // Add the default texture
            Material texture = this.getRenderMaterial(this.defaultTexture);
            if (Objects.equals(texture.texture(), MissingTextureAtlasSprite.getLocation())) {
                missingTextureErrors.add(Pair.of(this.defaultTexture, owner.getModelName()));
            }
            textures.add(owner.resolveTexture(this.defaultTexture));
            // Return the textures
            return textures;
        }
    }

    public static class DynamicTextureModel implements BakedModel, IRenderUtilities {
        private static final ModelProperty<ItemStack> PROPERTY_MATERIAL = TileEntityDynamicTexture.PROPERTY_MATERIAL;

        private final Map<TextureAtlasSprite, BakedModel> quadCache;

        private final BakedModel defaultModel;
        private final TextureAtlasSprite defaultSprite;

        private final List<BlockElement> parts;
        private final IModelConfiguration owner;
        private final ItemTransforms transforms;
        private final Function<Material, TextureAtlasSprite> spriteGetter;
        private final ModelState transform;
        private final ItemOverrides overrides;
        private final ResourceLocation modelLocation;

        private DynamicTextureModel(List<BlockElement> parts, IModelConfiguration owner, ItemTransforms transforms,
                                    Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform,
                                    ItemOverrides overrides, ResourceLocation modelLocation, String defaultTexture) {
            this.quadCache = Maps.newConcurrentMap();
            this.parts = parts;
            this.owner = owner;
            this.transforms = transforms;
            this.spriteGetter = spriteGetter;
            this.transform = transform;
            this.overrides = overrides;
            this.modelLocation = modelLocation;
            this.defaultSprite = this.spriteGetter.apply(this.getRenderMaterial(defaultTexture));
            this.defaultModel = this.bakeSubModel(this.getDefaultSprite());
        }

        public BakedModel getDefaultModel() {
            return this.defaultModel;
        }

        @Nonnull
        public TextureAtlasSprite getDefaultSprite() {
            return this.defaultSprite;
        }

        @Nonnull
        @SuppressWarnings("deprecation")
        protected TextureAtlasSprite getMaterialSprite(@Nullable ItemStack material) {
            if(material == null || material.isEmpty()) {
                return this.getDefaultSprite();
            }
            if(material.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) material.getItem()).getBlock();
                return this.getModelForState(block.defaultBlockState()).getParticleIcon();
            }
            return this.getDefaultSprite();
        }

        @Nonnull
        @Override
        public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData modelData) {
            return modelData;
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
            return this.getQuads(state, side, rand, this.getDefaultSprite());
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
            if (!extraData.hasProperty(PROPERTY_MATERIAL)) {
                return this.getQuads(state, side, rand);
            }
            TextureAtlasSprite sprite = this.getMaterialSprite(extraData.getData(PROPERTY_MATERIAL));
            return this.getQuads(state, side, rand, sprite);
        }

        @Nonnull
        @SuppressWarnings("deprecation")
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull TextureAtlasSprite material) {
            return this.getSubModel(material).getQuads(state, side, rand);
        }

        public BakedModel getSubModel(@Nonnull TextureAtlasSprite material) {
            if(material.getName().equals(this.getDefaultSprite().getName())) {
                return this.getDefaultModel();
            }
            return this.quadCache.computeIfAbsent(material, this::bakeSubModel);
        }

        protected BakedModel bakeSubModel(@Nonnull TextureAtlasSprite material) {
            // Initialize quad lists
            List<BakedQuad> generalQuads = Lists.newArrayList();
            Map<Direction, List<BakedQuad>> faceQuads = Maps.newEnumMap(Direction.class);
            Arrays.stream(Direction.values()).forEach((dir) -> faceQuads.put(dir, Lists.newArrayList()));
            // Iterate over all parts
            for (BlockElement part : this.parts) {
                // Iterate over the faces
                for (Direction direction : part.faces.keySet()) {
                    BlockElementFace face = part.faces.get(direction);
                    // Fetch the necessary texture
                    TextureAtlasSprite sprite;
                    if (face instanceof DynamicFace) {
                        sprite = material;
                    } else {
                        sprite = this.spriteGetter.apply(this.owner.resolveTexture(face.texture));
                    }
                    // Add the quad to the model builder
                    if (face.cullForDirection == null) {
                        generalQuads.add(BlockModel.makeBakedQuad(part, face, sprite, direction, this.transform, this.modelLocation));
                    } else {
                        faceQuads.get(this.transform.getRotation().rotateTransform(face.cullForDirection))
                                .add(BlockModel.makeBakedQuad(part, face, sprite, direction, this.transform, this.modelLocation));
                    }
                }
            }
            // Build the model
            return new SimpleBakedModel(
                    ImmutableList.copyOf(generalQuads),
                    ImmutableMap.copyOf(Maps.transformValues(faceQuads, ImmutableList::copyOf)),
                    this.owner.useSmoothLighting(), this.owner.isSideLit(), this.owner.isShadedInGui(),
                    material, this.getTransforms(), this.getOverrides());
        }

        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean usesBlockLight() {
            return false;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleIcon() {
            return this.getDefaultSprite();
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleIcon(@Nonnull IModelData extraData) {
            if (!extraData.hasProperty(PROPERTY_MATERIAL)) {
                return this.getParticleIcon();
            }
            return this.getMaterialSprite(extraData.getData(PROPERTY_MATERIAL));
        }

        @Nonnull
        @Override
        @Deprecated
        @SuppressWarnings("deprecation")
        public ItemTransforms getTransforms() {
            return this.transforms;
        }

        @Nonnull
        @Override
        public ItemOverrides getOverrides() {
            return this.overrides;
        }

        @Override
        public boolean isLayered() {
            return true;
        }

        @Override
        public List<Pair<BakedModel, RenderType>> getLayerModels(ItemStack stack, boolean fabulous) {
            return Collections.singletonList(Pair.of(
                    this.getSubModel(this.getMaterialSprite(this.getMaterialFromStack(stack))),
                    ItemBlockRenderTypes.getRenderType(stack, fabulous)));
        }

        @Nonnull
        protected ItemStack getMaterialFromStack(@Nonnull ItemStack stack) {
            if(stack.getItem() instanceof BlockItemDynamicTexture) {
                return ((BlockItemDynamicTexture) stack.getItem()).getMaterial(stack);
            }
            return ItemStack.EMPTY;
        }
    }
}
