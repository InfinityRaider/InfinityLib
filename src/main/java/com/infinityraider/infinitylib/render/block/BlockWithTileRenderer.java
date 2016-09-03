package com.infinityraider.infinitylib.render.block;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlockWithTile;
import com.infinityraider.infinitylib.block.blockstate.IBlockStateWithPos;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import java.util.*;

public class BlockWithTileRenderer<B extends BlockBase & ICustomRenderedBlockWithTile<T>, T extends TileEntityBase> extends TileEntitySpecialRenderer<T> implements IModel {
    private final B block;
    private final ITileRenderingHandler<B, T> renderer;

    public BlockWithTileRenderer(ITileRenderingHandler<B, T> renderer) {
        this.renderer = renderer;
        this.block = renderer.getBlock();
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
    public BakedBlockModel<B, T> bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new BakedBlockModel<>(block, format, renderer, bakedTextureGetter, renderer.doInventoryRendering());
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    @Override
    public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
        ITessellator tessellator = TessellatorVertexBuffer.getInstance(Tessellator.getInstance());
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        IBlockState state = world.getBlockState(pos);
        IBlockState extendedState = state.getBlock().getExtendedState(state, world, pos);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        tessellator.setColorRGBA(255, 255, 255, 255);

        this.renderer.renderWorldBlock(tessellator, world, pos, x, y, z, extendedState, block, te, true, partialTicks, destroyStage);

        tessellator.draw();

        GL11.glTranslated(-x, -y, -z);
        GL11.glPopMatrix();
    }

    public static class BakedBlockModel<B extends BlockBase & ICustomRenderedBlockWithTile<T>, T extends TileEntityBase> implements IBakedModel {
        private final B block;
        private final VertexFormat format;
        private final ITileRenderingHandler<B, T> renderer;
        private final Function<ResourceLocation, TextureAtlasSprite> textures;
        private final ItemRenderer itemRenderer;

        private Map<IBlockState, Map<EnumFacing, List<BakedQuad>>> cachedQuads;

        private BakedBlockModel(B block, VertexFormat format, ITileRenderingHandler<B, T> renderer, Function<ResourceLocation, TextureAtlasSprite> textures, boolean inventory) {
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
                BlockPos pos = ((IBlockStateWithPos<? extends IBlockState>) state).getPos();
                T tile = (T) world.getTileEntity(pos);
                if(tile != null) {
                    IBlockState extendedState = tile.getActualState( ((IBlockStateWithPos<? extends IBlockState>) state).getWrappedState());

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

                        this.renderer.renderWorldBlock(tessellator, world, pos, pos.getX(), pos.getY(), pos.getZ(), extendedState, block, tile, false, 1, 0);

                        cachedQuads.get(extendedState).put(side, tessellator.getQuads());

                        tessellator.draw();
                    }
                    return cachedQuads.get(extendedState).get(side);
                }
            }
            return Collections.emptyList();
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

    public static class ItemRenderer<B extends BlockBase & ICustomRenderedBlockWithTile<T>, T extends TileEntityBase> extends ItemOverrideList {
        private final ITileRenderingHandler<B, T> renderer;
        private final B block;
        private final T tile;
        private final VertexFormat format;
        private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

        public ItemRenderer(ITileRenderingHandler<B, T> renderer, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
            super(ImmutableList.of());
            this.renderer = renderer;
            this.tile = renderer.getTileEntity();
            this.block = renderer.getBlock();
            this.format = format;
            this.bakedTextureGetter = bakedTextureGetter;
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            return new BakedItemModel<>(world, block, tile, stack, entity, renderer, format, bakedTextureGetter);
        }
    }

    public static class BakedItemModel<B extends BlockBase & ICustomRenderedBlockWithTile<T>, T extends TileEntityBase> implements IBakedModel, IPerspectiveAwareModel {
        private final ITileRenderingHandler<B, T> renderer;
        private final B block;
        private final T tile;
        private final ItemStack stack;
        private final World world;
        private final EntityLivingBase entity;
        private ItemCameraTransforms.TransformType transformType;
        private final VertexFormat format;
        private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

        private BakedItemModel(World world, B block, T tile, ItemStack stack, EntityLivingBase entity, ITileRenderingHandler<B, T> renderer, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
            this.world = world;
            this.block = block;
            this.tile = tile;
            this.stack = stack;
            this.entity = entity;
            this.renderer = renderer;
            this.transformType = ItemCameraTransforms.TransformType.NONE;
            this.format = format;
            this.bakedTextureGetter = bakedTextureGetter;
        }

        private BakedItemModel<B, T> setTransformType(ItemCameraTransforms.TransformType type) {
            this.transformType = type;
            return this;
        }

        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
            ITessellator tessellator = TessellatorBakedQuad.getInstance().setTextureFunction(bakedTextureGetter).setCurrentFace(side);

            tessellator.startDrawingQuads(format);

            this.renderer.renderInventoryBlock(tessellator, world, state, block, tile, stack, entity, transformType);

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