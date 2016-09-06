/*
 */
package com.infinityraider.infinitylib.render.block;

import com.google.common.base.Function;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.block.blockstate.IBlockStateWithPos;
import com.infinityraider.infinitylib.render.DefaultTransforms;
import com.infinityraider.infinitylib.render.item.BakedInfItemSuperModel;
import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *
 * @author RlonRyan
 */
public class BakedInfBlockModel<B extends BlockBase & ICustomRenderedBlock> implements IBakedModel {
	
    private final B block;
    private final VertexFormat format;
    private final IBlockRenderingHandler<B> renderer;
    private final Function<ResourceLocation, TextureAtlasSprite> textures;
    private final BakedInfItemSuperModel itemRenderer;
    private final Map<IBlockState, Map<EnumFacing, List<BakedQuad>>> cachedQuads;

    BakedInfBlockModel(B block, VertexFormat format, IBlockRenderingHandler<B> renderer, Function<ResourceLocation, TextureAtlasSprite> textures, boolean inventory) {
        this.block = block;
        this.format = format;
        this.renderer = renderer;
        this.textures = textures;
        this.itemRenderer = inventory ? new BakedInfItemSuperModel(format, this.renderer, textures, DefaultTransforms::getBlockMatrix) : null;
        this.cachedQuads = new HashMap<>();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (state instanceof IBlockStateWithPos) {
            World world = Minecraft.getMinecraft().theWorld;
            IBlockState extendedState = ((IBlockStateWithPos<? extends IBlockState>) state).getWrappedState();
            BlockPos pos = ((IBlockStateWithPos<? extends IBlockState>) state).getPos();
            boolean update;
            if (!cachedQuads.containsKey(extendedState)) {
                cachedQuads.put(extendedState, new HashMap<>());
                update = true;
            } else {
                update = !cachedQuads.get(extendedState).containsKey(side);
            }
            if (update) {
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
        return itemRenderer.getOverrides();
    }
	
}
