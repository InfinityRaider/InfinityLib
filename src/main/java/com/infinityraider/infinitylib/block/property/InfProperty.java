package com.infinityraider.infinitylib.block.property;

import com.infinityraider.infinitylib.utility.DirectionalConnectivity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class InfProperty<T extends Comparable<T>> {
    private final Property<T> property;
    private final T defaultValue;
    private final MirrorHandler<T> mirrorHandler;
    private final RotationHandler<T> rotationHandler;

    private InfProperty(Property<T> property, T defaultValue, MirrorHandler<T> mirrorHandler, RotationHandler<T> rotationHandler) {
        this.property = property;
        this.defaultValue = defaultValue;
        this.mirrorHandler = mirrorHandler;
        this.rotationHandler = rotationHandler;
    }

    public final String getName() {
        return this.getProperty().getName();
    }

    public final T getDefaultValue() {
        return this.defaultValue;
    }

    public final Property<T> getProperty() {
        return this.property;
    }

    public final Collection<T> getAllowedValues() {
        return this.getProperty().getAllowedValues();
    }

    protected StateContainer.Builder<Block, BlockState> apply(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(this.getProperty());
        return builder;
    }

    public final BlockState apply(BlockState state) {
        return this.apply(state, this.getDefaultValue());
    }

    public final BlockState apply(BlockState state, T value) {
        return state.with(this.getProperty(), value);
    }

    public final T fetch(BlockState state) {
        return state.get(this.getProperty());
    }

    public final BlockState mimic(BlockState from, BlockState to) {
        return this.apply(to, this.fetch(from));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return this.apply(state, this.mirrorHandler.handle(mirror, this.fetch(state)));
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return this.apply(state, this.rotationHandler.handle(rotation, this.fetch(state)));
    }

    public static class Defaults {
        private Defaults() {}

        private static final InfProperty<Boolean> WATERLOGGED = Creators.create(BlockStateProperties.WATERLOGGED, false);
        private static final InfProperty<DirectionalConnectivity> CONNECTIVITY = Creators.create("connectivity", DirectionalConnectivity.NONE);

        public static InfProperty<Boolean> waterlogged() {
            return WATERLOGGED;
        }

        public static InfProperty<DirectionalConnectivity> connectivity() {
            return CONNECTIVITY;
        }
    }

    public static final class Creators {
        private Creators() {}

        public static InfProperty<Boolean> create(String name, boolean defaultValue) {
            return create(BooleanProperty.create(name), defaultValue);
        }

        public static InfProperty<Integer> create(String name, int def, int min, int max) {
            return create(IntegerProperty.create(name, min, max), def);
        }

        public static InfProperty<Direction> create(String name, Direction defaultValue) {
            return create(DirectionProperty.create(name, Direction.values()), defaultValue);
        }

        public static InfProperty<Direction> createHorizontals(String name, Direction defaultValue) {
            return create(DirectionProperty.create(name, Direction.Plane.HORIZONTAL), defaultValue);
        }

        public static InfProperty<Direction.Axis> create(String name, Direction.Axis defaultValue) {
            return create(EnumProperty.create(name, Direction.Axis.class), defaultValue);
        }

        public static InfProperty<Direction.Axis> createHorizontals(String name, Direction.Axis defaultValue) {
            return create(EnumProperty.create(name, Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z), defaultValue);
        }

        public static InfProperty<DirectionalConnectivity> create(String name, DirectionalConnectivity defaultValue) {
            return create(Properties.Connectivity.create(name), defaultValue);
        }

        public static <T extends Enum<T> & IStringSerializable> InfProperty<T> create(String name, Class<T> valueClass, T defaultValue) {
            return create(EnumProperty.create(name, valueClass), defaultValue);
        }

        public static <T extends Enum<T> & IStringSerializable> InfProperty<T> create(String name, Class<T> valueClass, T defaultValue, Collection<T> allowedValues) {
            return create(EnumProperty.create(name, valueClass, allowedValues), defaultValue);
        }

        public static <T extends Enum<T> & IStringSerializable> InfProperty<T> create(String name, Class<T> valueClass, T defaultValue, T... allowedValues) {
            return create(EnumProperty.create(name, valueClass, allowedValues), defaultValue);
        }

        public static <T extends Enum<T> & IStringSerializable> InfProperty<T> create(String name, Class<T> valueClass, T defaultValue, Predicate<T> allowedValues) {
            return create(EnumProperty.create(name, valueClass, allowedValues), defaultValue);
        }

        public static <T extends Comparable<T>> InfProperty<T> create(Property<T> property, T defaultValue) {
            return create(property, defaultValue, MirrorHandler.Handlers.defaultHandler(), RotationHandler.Handlers.defaultHandler());
        }

        public static <T extends Comparable<T>> InfProperty<T> create(Property<T> property, T defaultValue, MirrorHandler<T> mirrorHandler) {
            return create(property, defaultValue, mirrorHandler, RotationHandler.Handlers.defaultHandler());
        }

        public static InfProperty<Direction> create(DirectionProperty property, Direction defaultValue) {
            return create(property, defaultValue, MirrorHandler.Handlers.direction(), RotationHandler.Handlers.direction());
        }

        public static InfProperty<Direction.Axis> create(EnumProperty<Direction.Axis> property, Direction.Axis defaultValue) {
            return create(property, defaultValue, RotationHandler.Handlers.axis());
        }

        public static <T extends Comparable<T>> InfProperty<T> create(Property<T> property, T defaultValue, RotationHandler<T> rotationHandler) {
            return create(property, defaultValue, MirrorHandler.Handlers.defaultHandler(), rotationHandler);
        }

        public static <T extends Comparable<T>> InfProperty<T> create(Property<T> property, T defaultValue, MirrorHandler<T> mirrorHandler, RotationHandler<T> rotationHandler) {
            return new InfProperty<>(property, defaultValue, mirrorHandler, rotationHandler);
        }
    }

    public static class Properties {
        public static final class Connectivity extends Property<DirectionalConnectivity> {
            public static Property<DirectionalConnectivity> create(String name) {
                return new Connectivity(name);
            }

            private Connectivity(String name) {
                super(name, DirectionalConnectivity.class);
            }

            @Override
            public Collection<DirectionalConnectivity> getAllowedValues() {
                return DirectionalConnectivity.ALL;
            }

            @Override
            public String getName(DirectionalConnectivity value) {
                return value.toString();
            }

            @Override
            public Optional<DirectionalConnectivity> parseValue(String value) {
                return Optional.empty();
            }
        }
    }

}
