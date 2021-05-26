package com.infinityraider.infinitylib.block.property;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.utility.DirectionalConnectivity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.*;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public final class InfPropertyConfiguration {
    public static InfPropertyConfiguration empty() {
        return EMPTY;
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Set<InfProperty<?>> properties;

    private InfPropertyConfiguration(Set<InfProperty<?>> properties) {
        this.properties = ImmutableSet.copyOf(properties);
    }

    public boolean has(InfProperty<?> property) {
        return this.properties.contains(property);
    }

    public StateContainer.Builder<Block, BlockState> fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        this.properties.forEach((prop) -> prop.apply(builder));
        return builder;
    }

    public BlockState defineDefault(BlockState state) {
        for(InfProperty<?> property : this.properties) {
            state = property.apply(state);
        }
        return state;
    }

    public BlockState handleMirror(BlockState state, Mirror mirror) {
        for(InfProperty<?> property : this.properties) {
            state = property.mirror(state, mirror);
        }
        return state;
    }

    public BlockState handleRotation(BlockState state, Rotation rotation) {
        for(InfProperty<?> property : this.properties) {
            state = property.rotate(state, rotation);
        }
        return state;
    }

    public boolean isWaterLoggable() {
        return this.has(InfProperty.Defaults.waterlogged());
    }

    public boolean isLavaLoggable() {
        return this.has(InfProperty.Defaults.lavalogged());
    }

    public boolean isFluidLoggable() {
        return this.has(InfProperty.Defaults.fluidlogged());
    }

    private static final InfPropertyConfiguration EMPTY = builder().build();

    public static final class Builder {
        private final Set<InfProperty<?>> properties;

        private Builder() {
            this.properties = Sets.newIdentityHashSet();
        }

        public InfPropertyConfiguration build() {
            return new InfPropertyConfiguration(this.properties);
        }

        public Builder add(String name, boolean defaultValue) {
            return this.add(InfProperty.Creators.create(name, defaultValue));
        }

        public Builder add(String name, int def, int min, int max) {
            return this.add(InfProperty.Creators.create(name, def, min, max));
        }

        public Builder add(String name, Direction defaultValue) {
            return this.add(InfProperty.Creators.create(name, defaultValue));
        }

        public Builder addHorizontals(String name, Direction defaultValue) {
            return this.add(InfProperty.Creators.createHorizontals(name, defaultValue));
        }

        public Builder add(String name, Direction.Axis defaultValue) {
            return this.add(InfProperty.Creators.create(name, defaultValue));
        }

        public Builder addHorizontals(String name, Direction.Axis defaultValue) {
            return this.add(InfProperty.Creators.createHorizontals(name, defaultValue));
        }

        public Builder add(String name, DirectionalConnectivity defaultValue) {
            return this.add(InfProperty.Creators.create(name, defaultValue));
        }

        public <T extends Enum<T> & IStringSerializable> Builder add(String name, Class<T> valueClass, T defaultValue) {
            return this.add(InfProperty.Creators.create(EnumProperty.create(name, valueClass), defaultValue));
        }

        public <T extends Enum<T> & IStringSerializable> Builder add(String name, Class<T> valueClass, T defaultValue, Collection<T> allowedValues) {
            return this.add(InfProperty.Creators.create(EnumProperty.create(name, valueClass, allowedValues), defaultValue));
        }

        public <T extends Enum<T> & IStringSerializable> Builder add(String name, Class<T> valueClass, T defaultValue, T... allowedValues) {
            return this.add(InfProperty.Creators.create(EnumProperty.create(name, valueClass, allowedValues), defaultValue));
        }

        public <T extends Enum<T> & IStringSerializable> Builder add(String name, Class<T> valueClass, T defaultValue, Predicate<T> allowedValues) {
            return this.add(InfProperty.Creators.create(EnumProperty.create(name, valueClass, allowedValues), defaultValue));
        }

        public <T extends Comparable<T>> Builder add(Property<T> property, T defaultValue) {
            return add(InfProperty.Creators.create(property, defaultValue, MirrorHandler.Handlers.defaultHandler(), RotationHandler.Handlers.defaultHandler()));
        }

        public <T extends Comparable<T>> Builder add(Property<T> property, T defaultValue, MirrorHandler<T> mirrorHandler) {
            return add(InfProperty.Creators.create(property, defaultValue, mirrorHandler, RotationHandler.Handlers.defaultHandler()));
        }

        public Builder add(DirectionProperty property, Direction defaultValue) {
            return this.add(InfProperty.Creators.create(property, defaultValue, MirrorHandler.Handlers.direction(), RotationHandler.Handlers.direction()));
        }

        public Builder add(EnumProperty<Direction.Axis> property, Direction.Axis defaultValue) {
            return this.add(InfProperty.Creators.create(property, defaultValue, RotationHandler.Handlers.axis()));
        }

        public <T extends Comparable<T>> Builder add(Property<T> property, T defaultValue, RotationHandler<T> rotationHandler) {
            return this.add(InfProperty.Creators.create(property, defaultValue, MirrorHandler.Handlers.defaultHandler(), rotationHandler));
        }

        public <T extends Comparable<T>> Builder add(Property<T> property, T defaultValue, MirrorHandler<T> mirrorHandler, RotationHandler<T> rotationHandler) {
            return this.add(InfProperty.Creators.create(property, defaultValue, mirrorHandler, rotationHandler));
        }

        public <T extends Comparable<T>> Builder add(InfProperty<T> property) {
            this.properties.add(property);
            return this;
        }

        public Builder waterloggable() {
            return this.add(InfProperty.Defaults.waterlogged());
        }

        public Builder lavaloggable() {
            return this.add(InfProperty.Defaults.lavalogged());
        }

        public Builder fluidloggable() {
            return this.add(InfProperty.Defaults.fluidlogged());
        }

        public Builder connectivity() {
            return this.add(InfProperty.Defaults.connectivity());
        }
    }
}
