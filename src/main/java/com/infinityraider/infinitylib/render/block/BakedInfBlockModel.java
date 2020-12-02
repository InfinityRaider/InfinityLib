package com.infinityraider.infinitylib.render.block;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.render.item.BakedInfItemModel;
import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import com.infinityraider.infinitylib.utility.HashableBlockState;

import java.util.*;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;

public class BakedInfBlockModel<B extends BlockBase & ICustomRenderedBlock> implements IBakedModel {

    @Nonnull
    private final B block;
    @Nonnull
    private final VertexFormat format;
    @Nonnull
    private final IBlockRenderingHandler<B> renderer;
    @Nonnull
    private final Function<RenderMaterial, TextureAtlasSprite> textureFunction;
    @Nullable
    private final BakedInfItemModel itemRenderer;
    @Nonnull
    private final Map<HashableBlockState, ImmutableList<BakedQuad>> cachedQuads;

    @SuppressWarnings("unchecked")
    BakedInfBlockModel(
            @Nonnull B block,
            @Nonnull VertexFormat format,
            @Nonnull IBlockRenderingHandler<B> renderer,
            @Nonnull Function<RenderMaterial, TextureAtlasSprite> textureFunction,
            boolean hasInventoryRendering
    ) {
        // Validate and save parameters.
        this.block = Objects.requireNonNull(block, "The block for a BakedInfBlockModel must not be null!");
        this.format = Objects.requireNonNull(format, "The vertex format for a BakedInfBlockModel must not be null!");
        this.renderer = Objects.requireNonNull(renderer, "The renderer for a BakedInfBlockModel must not be null!");
        this.textureFunction = Objects.requireNonNull(textureFunction, "The texture provider for a BakedInfBlockModel must not be null!");

        // Create the inventory renderer instance, if needed.
        if (hasInventoryRendering) {
            this.itemRenderer = new BakedInfItemModel(format, this.renderer, textureFunction);
        } else {
            this.itemRenderer = null;
        }

        // Create the quad cache map.
        this.cachedQuads = new HashMap<>();
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        // Return the quads.
        return this.cachedQuads.computeIfAbsent(
                // The quad cache key.
                new HashableBlockState(state, side),
                // Function to create the quads if they are not in the cache.
                (key) -> createQuads(state, side, rand)
        );
    }

    @Nonnull
    private ImmutableList<BakedQuad> createQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        // Get the tessellator to render with.
        final TessellatorBakedQuad tessellator = TessellatorBakedQuad.getInstance();

        // Setup the tessellator
        tessellator.setCurrentFace(side);
        tessellator.setTextureFunction(this.textureFunction);
        tessellator.startDrawingQuads(this.format);

        // Have the renderer render the item using given tesselator.
        this.renderer.renderWorldBlockStatic(tessellator, state, block, side);
        
        // Get the quads from the tessellator.
        final ImmutableList<BakedQuad> result = tessellator.getQuads();
        
        // Flush the tessellator.
        tessellator.draw();
        
        // Return the created BakedQuad list.
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
    public boolean isSideLit() {
        return false;
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
    @Nonnull
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    @Nonnull
    public ItemOverrideList getOverrides() {
        // Depending on if has item renderer, return the override list.
        if (this.itemRenderer != null) {
            return itemRenderer.getOverrides();
        } else {
            return ItemOverrideList.EMPTY;
        }
    }

}
