package com.infinityraider.infinitylib.render.block;

import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.render.RenderUtil;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RenderBlockEmpty<B extends BlockBase & ICustomRenderedBlock> implements IBlockRenderingHandler<B> {

    public static <B extends BlockBase & ICustomRenderedBlock> RenderBlockEmpty<B> createEmptyRender(B block) {
        return new RenderBlockEmpty<>(block, false);
    }

    public static <B extends BlockBase & ICustomRenderedBlock> RenderBlockEmpty<B> createEmptyRender(B block, boolean inventory) {
        return new RenderBlockEmpty<>(block, inventory);
    }

    private final B block;
    private final boolean inventory;

    protected RenderBlockEmpty(B block, boolean inventory) {
        this.block = block;
        this.inventory = !inventory;
    }

    @Override
    public B getBlock() {
        return block;
    }

    @Override
    public List<RenderMaterial> getAllTextures() {
        return Collections.emptyList();
    }

    @Override
    public void renderWorldBlockStatic(ITessellator tessellator, BlockState state, B block, Direction side) {
    }

    @Override
    public void renderInventoryBlock(ITessellator tessellator, World world, BlockState state, B block,
                                     ItemStack stack, LivingEntity entity, ItemCameraTransforms.TransformType type) {
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return RenderUtil.getMissingSprite();
    }

    @Override
    public boolean applyAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean doInventoryRendering() {
        return inventory;
    }

}
