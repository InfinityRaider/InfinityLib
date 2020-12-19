package com.infinityraider.infinitylib.block.property;

import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

@FunctionalInterface
public interface RotationHandler<T extends Comparable<T>> {
    T handle(Rotation rotation, T value);

    class Handlers {
        @SuppressWarnings("unchecked")
        public static <T extends Comparable<T>> RotationHandler<T> defaultHandler() {
            return (RotationHandler<T>) NO_OP;
        }

        public static RotationHandler<Direction> direction() {
            return DIRECTION;
        }

        public static RotationHandler<Direction.Axis> axis() {
            return AXIS;
        }

        private static final RotationHandler<?> NO_OP = (rotation, value) -> value;
        private static final RotationHandler<Direction> DIRECTION = Rotation::rotate;
        private static final RotationHandler<Direction.Axis> AXIS = (rotation, value) -> {
            if(value.isVertical()) {
                return value;
            }
            if(value == Direction.Axis.X) {
                return rotation.rotate(Direction.EAST).getAxis();
            } else {
                return rotation.rotate(Direction.NORTH).getAxis();
            }
        };
    }
}
