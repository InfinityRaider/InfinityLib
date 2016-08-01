package com.infinityraider.infinitylib.render.block;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.block.blockstate.IBlockStateSpecial;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.render.DefaultTransforms;
import com.infinityraider.infinitylib.render.item.BakedInfItemSuperModel;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.*;

@SideOnly(Side.CLIENT)
public class BlockRenderer<B extends BlockBase & ICustomRenderedBlock<T>, T extends TileEntityBase> extends TileEntitySpecialRenderer<T> implements IModel {
    private final B block;
    private final IBlockRenderingHandler<B, T> renderer;

    public BlockRenderer(B block, IBlockRenderingHandler<B, T> renderer) {
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
		this.renderer.renderDynamic(tessellator, te, partialTicks, destroyStage);

        tessellator.draw();

        GL11.glTranslated(-x, -y, -z);
        GL11.glPopMatrix();
    }

    public static class BakedBlockModel<B extends BlockBase & ICustomRenderedBlock<T>, T extends TileEntityBase> implements IBakedModel {
        private final B block;
        private final VertexFormat format;
        private final IBlockRenderingHandler<B, T> renderer;
        private final Function<ResourceLocation, TextureAtlasSprite> textures;
        private final BakedInfItemSuperModel itemRenderer;

        private Map<EnumFacing, List<BakedQuad>> cachedQuads;
        private IBlockState prevState;

        private BakedBlockModel(B block, VertexFormat format, IBlockRenderingHandler<B, T> renderer, Function<ResourceLocation, TextureAtlasSprite> textures, boolean inventory) {
            this.block = block;
            this.format = format;
            this.renderer = renderer;
            this.textures = textures;
            this.itemRenderer = inventory ? new BakedInfItemSuperModel(format, this.renderer, textures, DefaultTransforms::getBlockMatrix) : null;
            this.cachedQuads = new HashMap<>();
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
            if((state instanceof IBlockStateSpecial)) {
                World world = Minecraft.getMinecraft().theWorld;
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
					this.renderer.renderStatic(tessellator, state);

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
            return itemRenderer.getOverrides();
        }
    }

}
