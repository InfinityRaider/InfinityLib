package com.infinityraider.infinitylib.render.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class BakedInfItemSubModel implements IBakedModel {

    @Nonnull
    private final BakedInfItemModel parent;
    @Nonnull
    private final ImmutableList<BakedQuad>[] faceQuads;

    @Nonnull
    private final ItemStack stack;
    @Nullable
    private final World world;
    @Nullable
    private final LivingEntity entity;

    public BakedInfItemSubModel(
            @Nonnull BakedInfItemModel parent,
            @Nonnull ItemStack stack,
            @Nullable World world,
            @Nullable LivingEntity entity
    ) {
        // Validate and save parameters.
        this.parent = Preconditions.checkNotNull(parent);
        this.stack = Preconditions.checkNotNull(stack);
        this.world = world;
        this.entity = entity;

        // Create sided quad cache.
        this.faceQuads = new ImmutableList[7];
    }

    @Override
    public ImmutableList<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        // Get the corresponding side index.
        final int index = (side == null) ? 6 : side.ordinal();

        // Attempt to fetch cached quads.
        ImmutableList<BakedQuad> quads = this.faceQuads[index];

        // If no cached quads exist, create them.
        if (quads == null) {
            // Get the instance of the tessellator to use.
            final TessellatorBakedQuad tessellator = TessellatorBakedQuad.getInstance();

            // Setup the tessellator.
            tessellator.setCurrentFace(side);
            tessellator.setTextureFunction(this.parent.textureFunction);
            tessellator.startDrawingQuads(this.parent.format);

            // Have the renderer render the item using given tesselator.
            this.parent.renderer.renderItem(tessellator, world, stack, entity);

            // Get the quads from the tesselator.
            quads = tessellator.getQuads();

            // Flush the tessellator.
            tessellator.draw();

            // Update the cache.
            this.faceQuads[index] = quads;
        }

        // Return the quad list.
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.parent.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.parent.isGui3d();
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.parent.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.parent.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.parent.getItemCameraTransforms();
    }

    @Override
    @Nonnull
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }
}
