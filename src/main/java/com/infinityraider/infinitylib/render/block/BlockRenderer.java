package com.infinityraider.infinitylib.render.block;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.block.blockstate.IBlockStateSpecial;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.*;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import java.util.*;

@SideOnly(Side.CLIENT)
public class BlockRenderer<T extends TileEntityBase> extends TileEntitySpecialRenderer<T> implements IModel {
    private final ICustomRenderedBlock<T> block;
    private final IBlockRenderingHandler<T> renderer;

    public BlockRenderer(ICustomRenderedBlock<T> block, IBlockRenderingHandler<T> renderer) {
        this.block = block;
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
    public BakedBlockModel<T> bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
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
        Block block = state.getBlock();
        IBlockState extendedState = block.getExtendedState(state, world, pos);

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

    public static class BakedBlockModel<T extends TileEntityBase> implements IBakedModel {
        private final ICustomRenderedBlock<T> block;
        private final VertexFormat format;
        private final IBlockRenderingHandler<T> renderer;
        private final Function<ResourceLocation, TextureAtlasSprite> textures;
        private final ItemRenderer itemRenderer;

        private Map<EnumFacing, List<BakedQuad>> cachedQuads;
        private IBlockState prevState;

        private BakedBlockModel(ICustomRenderedBlock<T> block, VertexFormat format, IBlockRenderingHandler<T> renderer, Function<ResourceLocation, TextureAtlasSprite> textures, boolean inventory) {
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
            if((state instanceof IBlockStateSpecial)) {
                World world = Minecraft.getMinecraft().theWorld;
                Block block = state.getBlock();
                T tile = ((IBlockStateSpecial<T, ? extends IBlockState>) state).getTileEntity();
                IBlockState extendedState = ((IBlockStateSpecial<T, ? extends IBlockState>) state).getWrappedState();
                BlockPos pos = ((IBlockStateSpecial<T, ? extends IBlockState>) state).getPos();

                boolean update;
                if(tile == null) {
                    update = !extendedState.equals(prevState);
                } else {
                    update = !cachedQuads.containsKey(side) || this.block.needsRenderUpdate(world, pos, extendedState, tile);
                }

                if(update) {
                    TessellatorBakedQuad tessellator = TessellatorBakedQuad.getInstance().setTextureFunction(this.textures).setCurrentFace(side);

                    tessellator.startDrawingQuads(this.format);

                    this.renderer.renderWorldBlock(tessellator, world, pos, pos.getX(), pos.getY(), pos.getZ(), extendedState, block, tile, false, 1, 0);

                    cachedQuads.put(side, tessellator.getQuads());
                    prevState = extendedState;

                    tessellator.draw();
                }

            } else {
                return ImmutableList.of();
            }
            return cachedQuads.get(side);
        }

        @Override
        public boolean isAmbientOcclusion() {
            return true;
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

    public static class ItemRenderer<T extends TileEntityBase> extends ItemOverrideList {
        private final IBlockRenderingHandler<T> renderer;
        private final Block block;
        private final T tile;
        private final VertexFormat format;
        private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

        public ItemRenderer(IBlockRenderingHandler<T> renderer, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
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

    public static class BakedItemModel<T extends TileEntityBase> implements IBakedModel, IPerspectiveAwareModel {
        private final IBlockRenderingHandler<T> renderer;
        private final Block block;
        private final T tile;
        private final ItemStack stack;
        private final World world;
        private final EntityLivingBase entity;
        private ItemCameraTransforms.TransformType transformType;
        private final VertexFormat format;
        private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

        private BakedItemModel(World world, Block block, T tile, ItemStack stack, EntityLivingBase entity, IBlockRenderingHandler<T> renderer, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
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

        private BakedItemModel<T> setTransformType(ItemCameraTransforms.TransformType type) {
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
