package com.infinityraider.infinitylib.render.block;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.block.blockstate.IBlockStateWithPos;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.*;

@SideOnly(Side.CLIENT)
public class BlockRenderer<B extends BlockBase & ICustomRenderedBlock> implements IModel {
    private final B block;
    private final IBlockRenderingHandler<B> renderer;

    public BlockRenderer(IBlockRenderingHandler<B> renderer) {
        this.block = renderer.getBlock();
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
    public BakedBlockModel<B> bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new BakedBlockModel<>(block, format, renderer, bakedTextureGetter, renderer.doInventoryRendering());
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    public static class BakedBlockModel<B extends BlockBase & ICustomRenderedBlock> implements IBakedModel {
        private final B block;
        private final VertexFormat format;
        private final IBlockRenderingHandler<B> renderer;
        private final Function<ResourceLocation, TextureAtlasSprite> textures;
        private final ItemRenderer itemRenderer;

        private Map<IBlockState, Map<EnumFacing, List<BakedQuad>>> cachedQuads;

        private BakedBlockModel(B block, VertexFormat format, IBlockRenderingHandler<B> renderer, Function<ResourceLocation, TextureAtlasSprite> textures, boolean inventory) {
            this.block = block;
            this.format = format;
            this.renderer = renderer;
            this.textures = textures;
            this.itemRenderer = inventory ? new ItemRenderer<>(this.renderer, format, textures) : null;
            this.cachedQuads = new HashMap<>();
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
            if((state instanceof IBlockStateWithPos)) {
                World world = Minecraft.getMinecraft().theWorld;
                IBlockState extendedState = ((IBlockStateWithPos<? extends IBlockState>) state).getWrappedState();
                BlockPos pos = ((IBlockStateWithPos<? extends IBlockState>) state).getPos();

                boolean update;
                if(!cachedQuads.containsKey(extendedState)) {
                    cachedQuads.put(extendedState, new HashMap<>());
                    update = true;
                } else {
                    update = !cachedQuads.get(extendedState).containsKey(side);
                }

                if(update) {
                    TessellatorBakedQuad tessellator = TessellatorBakedQuad.getInstance().setTextureFunction(this.textures).setCurrentFace(side);

                    tessellator.startDrawingQuads(this.format);

                    this.renderer.renderWorldBlock(tessellator, world, pos, extendedState, block);

                    cachedQuads.get(extendedState).put(side, tessellator.getQuads());

                    tessellator.draw();
                }
                return cachedQuads.get(extendedState).get(side);
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public boolean isAmbientOcclusion() {
            return renderer.applyAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return renderer.doInventoryRendering();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return renderer.getIcon();
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public ItemOverrideList getOverrides() {
            return itemRenderer;
        }
    }

    public static class ItemRenderer<B extends BlockBase & ICustomRenderedBlock> extends ItemOverrideList {
        private final IBlockRenderingHandler<B> renderer;
        private final B block;
        private final VertexFormat format;
        private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

        public ItemRenderer(IBlockRenderingHandler<B> renderer, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
            super(ImmutableList.of());
            this.renderer = renderer;
            this.block = renderer.getBlock();
            this.format = format;
            this.bakedTextureGetter = bakedTextureGetter;
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            return new BakedItemModel<>(world, block, stack, entity, renderer, format, bakedTextureGetter);
        }
    }

    public static class BakedItemModel<B extends BlockBase & ICustomRenderedBlock> implements IBakedModel, IPerspectiveAwareModel {
        private final IBlockRenderingHandler<B> renderer;
        private final B block;
        private final ItemStack stack;
        private final World world;
        private final EntityLivingBase entity;
        private ItemCameraTransforms.TransformType transformType;
        private final VertexFormat format;
        private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

        private BakedItemModel(World world, B block, ItemStack stack, EntityLivingBase entity, IBlockRenderingHandler<B> renderer, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
            this.world = world;
            this.block = block;
            this.stack = stack;
            this.entity = entity;
            this.renderer = renderer;
            this.transformType = ItemCameraTransforms.TransformType.NONE;
            this.format = format;
            this.bakedTextureGetter = bakedTextureGetter;
        }

        private BakedItemModel<B> setTransformType(ItemCameraTransforms.TransformType type) {
            this.transformType = type;
            return this;
        }

        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
            ITessellator tessellator = TessellatorBakedQuad.getInstance().setTextureFunction(bakedTextureGetter).setCurrentFace(side);

            tessellator.startDrawingQuads(format);

            this.renderer.renderInventoryBlock(tessellator, world, state, block, stack, entity, transformType);

            List<BakedQuad> list = tessellator.getQuads();
            tessellator.draw();
            return list;
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
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
            return new ImmutablePair<>(this.setTransformType(cameraTransformType), null);
        }
    }
}
