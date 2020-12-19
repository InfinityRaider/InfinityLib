package com.infinityraider.infinitylib.item;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.infinitylib.render.item.InfItemRenderer;
import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Set;

public interface IInfinityItem extends IInfinityRegistrable<Item> {

    /**
     * Method to define model properties for use in the model json
     * @return non-null list of the properties
     */
    @OnlyIn(Dist.CLIENT)
    default Set<InfinityItemProperty> getModelProperties() {
        return ImmutableSet.of();
    }

    /**
     * Method to fetch the Item renderer to use to dynamically render this item from code.
     * You must pass your Item's properties through the setItemRenderer method in your mod's Proxy for this method to be used
     * The model json must have the parent defined as "parent": "builtin/entity"
     *
     * @return Item renderer object for this item, or null if no custom renderer is needed.
     */
    @OnlyIn(Dist.CLIENT)
    default InfItemRenderer getItemRenderer() {
        return null;
    }
}
