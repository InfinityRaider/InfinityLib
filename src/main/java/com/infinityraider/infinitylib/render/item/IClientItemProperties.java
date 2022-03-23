package com.infinityraider.infinitylib.render.item;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.infinitylib.item.InfinityItemProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public interface IClientItemProperties {
    IClientItemProperties DEFAULT = new IClientItemProperties() {};

    /**
     * Method to define model properties for use in the model json
     * @return non-null list of the properties
     */
    @Nonnull
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
    @Nullable
    @OnlyIn(Dist.CLIENT)
    default InfItemRenderer getItemRenderer() {
        return null;
    }
}
