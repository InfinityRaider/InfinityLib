package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.utility.registration.IInfinityRegistrable;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IInfinityBlock extends IInfinityRegistrable<Block> {

    /**
     * Retrieves the block's item form, if the block has one.
     *
     * @param <T>
     * @return an optional containing the block's item form, or the empty optional.
     */
    @Nonnull
    default <T extends BlockItem & IInfinityItem> Optional<T> getBlockItem() {
        return Optional.empty();
    }

    default void spawnItem(Level world, BlockPos pos, ItemStack stack) {
        if(world == null || world.isClientSide()) {
            return;
        }
        world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
    }

    @OnlyIn(Dist.CLIENT)
    default RenderType getRenderType() {
        return RenderType.solid();
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    default BlockColor getColor() {
        return null;
    }

}
