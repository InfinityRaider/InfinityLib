package com.infinityraider.infinitylib.block.blockstate;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class InfinityProperty<T extends Comparable<T>> {
	private final IProperty<T> property;
	private final T defaultValue;

	public InfinityProperty(IProperty<T> property, T defaultValue) {
		this.property = property;
		this.defaultValue = defaultValue;
	}

	public IProperty<T> getProperty() {
		return property;
	}

	public T getDefault() {
		return defaultValue;
	}

	public IBlockState applyToBlockState(IBlockState state) {
		return applyToBlockState(state, getDefault());
	}

	public IBlockState applyToBlockState(IBlockState state, T value) {
		return state.withProperty(getProperty(), value);
	}

	public T getValue(IBlockState state) {
		if (state.getPropertyKeys().contains(this.getProperty())) {
			return state.getValue(getProperty());
		} else {
			return this.defaultValue;
		}
	}
}
