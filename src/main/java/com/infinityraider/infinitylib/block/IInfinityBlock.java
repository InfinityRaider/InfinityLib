package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

    default void spawnItem(World world, BlockPos pos, ItemStack stack) {
        if(world == null || world.isRemote()) {
            return;
        }
        world.addEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
    }

    @OnlyIn(Dist.CLIENT)
    default RenderType getRenderType() {
        return RenderType.getSolid();
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    default IBlockColor getColor() {
        return null;
    }

}
