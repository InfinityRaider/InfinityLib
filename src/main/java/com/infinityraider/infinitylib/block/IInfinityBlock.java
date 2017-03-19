package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.utility.IToggleable;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public interface IInfinityBlock extends IToggleable {
    String getInternalName();

    default List<String> getOreTags() {
        return Collections.emptyList();
    }

    /**
     * Method should be implemented by default in any class extending Block
     */
    ResourceLocation getRegistryName();
}

