package com.infinityraider.infinitylib.item;

import net.minecraft.item.Item;
import java.util.List;

public abstract class ItemBase extends Item implements IItemWithModel {
    private final String internalName;

    public ItemBase(String name) {
        super();
        this.internalName = name;
    }

    public boolean isEnabled() {
        return true;
    }

    public String getInternalName() {
        return internalName;
    }

    public abstract List<String> getOreTags();
}