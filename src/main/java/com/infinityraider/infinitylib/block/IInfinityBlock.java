package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.BlockItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import javax.annotation.Nonnull;

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

    @OnlyIn(Dist.CLIENT)
    default RenderType getRenderType() {
        return RenderType.getSolid();
    }

}
