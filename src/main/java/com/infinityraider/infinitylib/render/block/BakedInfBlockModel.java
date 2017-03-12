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
import net.minecraftforge.common.property.IExtendedBlockState;

/*
 * Lets try this instead.
 */
public class BakedInfBlockModel<B extends BlockBase & ICustomRenderedBlock> implements IBakedModel {

    private final B block;
    private final VertexFormat format;
    private final IBlockRenderingHandler<B> renderer;
    private final Function<ResourceLocation, TextureAtlasSprite> textures;
    private final BakedInfItemSuperModel itemRenderer;
    private final Map<Integer, Map<EnumFacing, List<BakedQuad>>> cachedQuads;

    @SuppressWarnings("unchecked")
    BakedInfBlockModel(B block, VertexFormat format, IBlockRenderingHandler<B> renderer, Function<ResourceLocation, TextureAtlasSprite> textures, boolean inventory) {
        this.block = Objects.requireNonNull(block, "The block for a BakedInfBlockModel must not be null!");
        this.format = Objects.requireNonNull(format, "The vertex format for a BakedInfBlockModel must not be null!");
        this.renderer = Objects.requireNonNull(renderer, "The renderer for a BakedInfBlockModel must not be null!");
        this.textures = Objects.requireNonNull(textures, "The texture provider for a BakedInfBlockModel must not be null!");
        this.itemRenderer = inventory ? new BakedInfItemSuperModel(format, this.renderer, textures, DefaultTransforms::getBlockMatrix) : null;
        this.cachedQuads = new HashMap<>();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<BakedQuad> getQuads(IBlockState state, @Nullable EnumFacing side, long rand) {
        // Return the quads.
        return cachedQuads
                // Fetch the map.
                .computeIfAbsent(hashState(state), (e) -> new HashMap<>())
                // Fetch the quads.
                .computeIfAbsent(side, (s) -> createQuads(state, s, rand));
    }
    
    private static int hashState(IBlockState state) {
        int hash = 7;
        hash = 31 * hash + state.getProperties().hashCode();
        hash = 31 * hash + ((state instanceof IExtendedBlockState) ? ((IExtendedBlockState)state).getUnlistedProperties().hashCode() : 0);
        return hash;
    }

    private List<BakedQuad> createQuads(IBlockState state, EnumFacing side, long rand) {
        TessellatorBakedQuad tessellator = TessellatorBakedQuad.getInstance().setTextureFunction(this.textures).setCurrentFace(side);
        tessellator.startDrawingQuads(this.format);
        this.renderer.renderWorldBlockStatic(tessellator, state, block, side);
        final List<BakedQuad> result = tessellator.getQuads();
        tessellator.draw();
        return result;
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
