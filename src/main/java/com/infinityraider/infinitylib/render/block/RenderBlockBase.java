package com.infinityraider.infinitylib.render.block;

import javax.annotation.Nonnull;

import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.render.RenderUtilBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockBase<B extends BlockBase & ICustomRenderedBlock<T>, T extends TileEntityBase> extends RenderUtilBase implements IBlockRenderingHandler<B, T> {
    private final B block;
    private final T dummy;
    private final boolean inv;
    private final boolean statRender;
    private final boolean dynRender;

    protected RenderBlockBase(B block, T te, boolean inv, boolean statRender, boolean dynRender) {
        this.block = block;
        this.dummy = te;
        this.inv = inv;
        this.statRender = statRender;
        this.dynRender = dynRender;
    }

    @Override
    public B getBlock() {
        return block;
    }

    @Override
    @Nonnull public T getTileEntity() {
        return dummy;
    }

    @Override
    public List<ResourceLocation> getAllTextures() {
        return getBlock().getTextures();
    }

    @Override
    public boolean doInventoryRendering() {
        return inv;
    }

    @Override
    public boolean hasDynamicRendering() {
        return dynRender;
    }

    @Override
    public boolean hasStaticRendering() {
        return statRender;
    }
}
