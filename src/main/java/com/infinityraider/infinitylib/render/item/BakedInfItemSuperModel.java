/*
 */
package com.infinityraider.infinitylib.render.item;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Function;
import javax.vecmath.Matrix4f;

import com.infinityraider.infinitylib.render.DefaultTransforms;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

@SideOnly(Side.CLIENT)
public class BakedInfItemSuperModel<T extends IItemRenderingHandler> implements IBakedModel, IItemOverriden {

    protected final VertexFormat format;
    protected final T renderer;
    protected final Function<ResourceLocation, TextureAtlasSprite> textures;
    protected final DefaultTransforms.Transformer transformer;
    protected final ItemOverrideList overrides;

    public BakedInfItemSuperModel(VertexFormat format, T renderer, Function<ResourceLocation, TextureAtlasSprite> textures) {
        this.format = format;
        this.renderer = renderer;
        this.textures = textures;
        this.transformer = this.renderer.getPerspectiveTransformer();
        this.overrides = new IItemOverriden.Wrapper(this);
    }

    public BakedInfItemSuperModel(VertexFormat format, T renderer, Function<ResourceLocation, TextureAtlasSprite> textures, DefaultTransforms.Transformer transformer) {
        this.format = format;
        this.renderer = renderer;
        this.textures = textures;
        this.transformer = transformer;
        this.overrides = new IItemOverriden.Wrapper(this);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
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
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType transform) {
        return Pair.of(this, this.transformer.apply(transform));
    }

    @Override
    public final BakedInfItemModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
        return new BakedInfItemModel(this, format, textures, world, stack, entity, renderer);
    }

    @Override
    public final ItemOverrideList getOverrides() {
        return this.overrides;
    }

}
