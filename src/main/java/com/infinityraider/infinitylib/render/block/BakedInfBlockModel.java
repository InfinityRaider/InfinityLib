/*
 */
package com.infinityraider.infinitylib.render.block;

import com.google.common.base.Function;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.render.DefaultTransforms;
import com.infinityraider.infinitylib.render.item.BakedInfItemSuperModel;
import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

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
    private final Map<IBlockState, List<BakedQuad>[]> cachedQuads;

    @SuppressWarnings("unchecked")
    BakedInfBlockModel(B block, VertexFormat format, IBlockRenderingHandler<B> renderer, Function<ResourceLocation, TextureAtlasSprite> textures, boolean inventory) {
        if(renderer == null) {
            throw new NullPointerException("Renderer may not be null");
        }
        this.block = block;
        this.format = format;
        this.renderer = renderer;
        this.textures = textures;
        this.itemRenderer = inventory ? new BakedInfItemSuperModel(format, this.renderer, textures, DefaultTransforms::getBlockMatrix) : null;
        this.cachedQuads = new HashMap<>();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<BakedQuad> getQuads(IBlockState state, @Nullable EnumFacing side, long rand) {
            boolean update;
            // Since strange things are afoot here.
            Objects.requireNonNull(cachedQuads);
            Objects.requireNonNull(state);
            
            // Since side may be null.
            int index = (side == null) ? EnumFacing.values().length : side.ordinal();
            
            if (!cachedQuads.containsKey(state)) {
                cachedQuads.put(state, new List[EnumFacing.values().length + 1]);
                update = true;
            } else {
                update = cachedQuads.get(state)[index] == null;
            }
            if (update) {
                TessellatorBakedQuad tessellator = TessellatorBakedQuad.getInstance().setTextureFunction(this.textures).setCurrentFace(side);
                tessellator.startDrawingQuads(this.format);
                this.renderer.renderWorldBlockStatic(tessellator, state, block, side);
                cachedQuads.get(state)[index] = tessellator.getQuads();
                tessellator.draw();
            }
            return cachedQuads.get(state)[index];
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
