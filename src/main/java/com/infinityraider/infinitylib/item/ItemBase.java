package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.utility.ModHelper;
import net.minecraft.item.Item;

import java.util.Collections;
import java.util.List;

public abstract class ItemBase extends Item implements IInfinityItem {

	private final String internalName;
	private final boolean isModelVanilla;
	protected final String[] varients;

	public ItemBase(String name, boolean modelVanilla, String... varients) {
		super();
		this.internalName = name;
		this.isModelVanilla = modelVanilla;
		this.varients = varients;
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVanillaModel() {
		return isModelVanilla;
	}

	public String getInternalName() {
		return internalName;
	}

	public List<String> getIgnoredNBT() {
		return Collections.emptyList();
	}

	public abstract List<String> getOreTags();

	@Override
	public void registerItemRenderer() {
		ModHelper.registerItemModels(this, varients);
	}
	
}
