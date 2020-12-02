package com.infinityraider.infinitylib.render.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.infinityraider.infinitylib.render.DefaultTransforms;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BakedInfItemModel implements IBakedModel, IItemOverriden {

    @Nonnull
    protected final VertexFormat format;
    @Nonnull
    protected final IItemRenderingHandler renderer;
    @Nonnull
    protected final Function<RenderMaterial, TextureAtlasSprite> textureFunction;

    @Nonnull
    protected final DefaultTransforms.Transformer transformer;

    @Nonnull
    private final ItemOverrideList overrides;
    @Nonnull
    private final Map<Object, BakedInfItemSubModel> cache;

    public BakedInfItemModel(
            @Nonnull VertexFormat format,
            @Nonnull IItemRenderingHandler renderer,
            @Nonnull Function<RenderMaterial, TextureAtlasSprite> textures
    ) {
        // Validate and save parameters.
        this.format = Preconditions.checkNotNull(format);
        this.renderer = Preconditions.checkNotNull(renderer);
        this.textureFunction = Preconditions.checkNotNull(textures);

        // Save the transformer.
        this.transformer = Preconditions.checkNotNull(renderer.getPerspectiveTransformer());

        // Create cache and override wrapper instance.
        this.overrides = new IItemOverriden.Wrapper(this);
        this.cache = new HashMap<>();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return ImmutableList.of();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
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

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public final BakedInfItemSubModel handleItemState(IBakedModel originalModel, ItemStack stack, ClientWorld world, LivingEntity entity) {
        return this.cache.computeIfAbsent(
                // Get the respective cache key for the item model.
                this.renderer.getItemQuadsCacheKey(world, stack, entity),
                // Compute a new submodel, if one does not already exist for the given cache key.
                k -> new BakedInfItemSubModel(this, stack, world, entity)
        );
    }

    @Override
    public final ItemOverrideList getOverrides() {
        return this.overrides;
    }

}
