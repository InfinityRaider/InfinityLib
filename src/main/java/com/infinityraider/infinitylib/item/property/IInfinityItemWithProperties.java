package com.infinityraider.infinitylib.item.property;

import com.infinityraider.infinitylib.item.IInfinityItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Set;

public interface IInfinityItemWithProperties extends IInfinityItem {
    @OnlyIn(Dist.CLIENT)
    Set<InfinityItemProperty> getProperties();
}
