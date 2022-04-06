package com.infinityraider.infinitylib.block.tile;

import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface IInfinityTileEntityType extends IInfinityRegistrable<BlockEntityType<?>> {
    @Nullable
    @OnlyIn(Dist.CLIENT)
    ITileRenderer<? extends BlockEntity> getRenderer();

    boolean isTicking();
}
