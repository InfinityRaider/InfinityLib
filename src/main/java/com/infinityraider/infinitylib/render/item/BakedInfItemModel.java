/*
 */
package com.infinityraider.infinitylib.render.item;

import java.util.List;
import javax.vecmath.Matrix4f;

import com.infinityraider.infinitylib.render.tessellation.TessellatorBakedQuad;
import java.util.Collections;
import java.util.Objects;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author RlonRyan
 */
@SideOnly(Side.CLIENT)
public class BakedInfItemModel implements IBakedModel {

    private final BakedInfItemSuperModel parent;
    private final ItemStack stack;
    private final World world;
    private final EntityLivingBase entity;
    private final List<BakedQuad>[] faceQuads;

    public BakedInfItemModel(BakedInfItemSuperModel parent, World world, ItemStack stack, EntityLivingBase entity, IItemRenderingHandler renderer) {
        this.parent = parent;
        this.world = world;
        this.stack = stack;
        this.entity = entity;
        this.faceQuads = new List[7];
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        synchronized(this.faceQuads) {
            final int index = (side == null) ? 6 : side.ordinal();
            List<BakedQuad> quads = this.faceQuads[index];
            if (quads == null) {
                quads = createQuads(side);
                this.faceQuads[index] = quads;
            }
            return quads;
        }
    }
    
    private List<BakedQuad> createQuads(EnumFacing side) {
        List<BakedQuad> list;

        final TessellatorBakedQuad tessellator = TessellatorBakedQuad.getInstance();
        tessellator.setCurrentFace(side);
        tessellator.setTextureFunction(this.parent.textures);
        tessellator.startDrawingQuads(this.parent.format);
        this.parent.renderer.renderItem(tessellator, world, stack, entity);
        list = tessellator.getQuads();
        tessellator.draw();

        return Collections.unmodifiableList(list);
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
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType transform) {
        return Pair.of(this, this.parent.transformer.apply(transform));
    }
}
