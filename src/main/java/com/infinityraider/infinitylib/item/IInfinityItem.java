package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.render.item.IClientItemProperties;
import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Server-safe method to initialize client side stuff
 */
public interface IInfinityItem extends IInfinityRegistrable<Item> {
    default Supplier<IClientItemProperties> getClientItemProperties() {
        return () -> IClientItemProperties.DEFAULT;
    }
}
