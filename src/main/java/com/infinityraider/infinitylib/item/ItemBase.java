package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class ItemBase extends Item implements IInfinityItem {

	private final String internalName;

	public ItemBase(String name, Properties properties) {
		super(properties);
		this.internalName = name;
	}

	public boolean isEnabled() {
		return true;
	}

	@Nonnull
	public String getInternalName() {
		return internalName;
	}

	@Override
	public final void initializeClient(@Nonnull Consumer<IItemRenderProperties> consumer) {
		InfinityLib.instance.proxy().initItemRenderer(consumer);
	}
}
