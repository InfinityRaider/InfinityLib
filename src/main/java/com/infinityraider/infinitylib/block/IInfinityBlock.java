package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.utility.IToggleable;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public interface IInfinityBlock extends IToggleable {
    String getInternalName();

    /**
     * Retrieves the block's ItemBlock class, as a generic class bounded by the
     * ItemBlock class.
     *
     * @return the block's class, may be null if no specific ItemBlock class is
     * desired.
     */
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
}

