package com.infinityraider.infinitylib.block.tile;

import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface IInfinityTileEntityType extends IInfinityRegistrable<TileEntityType<?>> {
    @Nullable
    @OnlyIn(Dist.CLIENT)
    ITileRenderer<? extends TileEntity> getRenderer();
}
