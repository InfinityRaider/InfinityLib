package com.infinityraider.infinitylib.block.blockstate;

import net.minecraft.block.BlockState;
import net.minecraft.state.Property;

public class InfinityProperty<T extends Comparable<T>> {

    private final Property<T> property;
    private final T defaultValue;

    public InfinityProperty(Property<T> property, T defaultValue) {
        this.property = property;
        this.defaultValue = defaultValue;
    }

    public Property<T> getProperty() {
        return property;
    }

    public T getDefault() {
        return defaultValue;
    }

    public <B extends BlockState> B applyToBlockState(B state) {
        return applyToBlockState(state, getDefault());
    }

    public <B extends BlockState> B applyToBlockState(B state, T value) {
        return (B) state.with(getProperty(), value);
    }

    public T getValue(BlockState state) {
        if (state.getProperties().contains(this.getProperty())) {
            return state.get(getProperty());
        } else {
            return this.defaultValue;
        }
    }
}
