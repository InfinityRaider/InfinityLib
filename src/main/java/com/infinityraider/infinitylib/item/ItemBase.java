package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;

public abstract class ItemBase extends Item implements IInfinityItem {

	private final String internalName;

	public ItemBase(String name, Properties properties) {
		super(InfinityLib.instance.proxy().setItemRenderer(properties));
		this.internalName = name;
	}

	public boolean isEnabled() {
		return true;
	}

	@Nonnull
	public String getInternalName() {
		return internalName;
	}
}
