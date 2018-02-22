package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.utility.IToggleable;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface IInfinityBlock extends IToggleable {
    String getInternalName();

    /**
     * Retrieves the block's ItemBlock class, as a generic class bounded by the
     * ItemBlock class.
     *
     * @return the block's class, may be null if no specific ItemBlock class is
     * desired.
     */
    @Deprecated
    default Class<? extends ItemBlock> getItemBlockClass() {
        return null;
    }

    default List<String> getOreTags() {
        return Collections.emptyList();
    }

    /**
     * Method should be implemented by default in any class extending Block
     */
    ResourceLocation getRegistryName();
    
    /**
     * Retrieves the block's item form, if the block has one.
     * 
     * @param <T>
     * @return an optional containing the block's item form, or the empty optional.
     */
    default <T extends ItemBlock & IInfinityItem> Optional<T> getItemBlock() {
        return Optional.empty();
    }
}

