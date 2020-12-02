package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public interface IInfinityItem extends IInfinityRegistrable<Item> {

    /**
     * This method should be standard implemented in every Item, used for default implementation in sub-interfaces
     */
    ResourceLocation getRegistryName();

	default List<String> getTagNames() {
		return Collections.emptyList();
	}
}
