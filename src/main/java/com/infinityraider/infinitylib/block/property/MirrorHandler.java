package com.infinityraider.infinitylib.block.property;

import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;

@FunctionalInterface
public interface MirrorHandler<T> {
    T handle(Mirror mirror, T value);

    class Handlers {
        @SuppressWarnings("unchecked")
        public static <T extends Comparable<T>> MirrorHandler<T> defaultHandler() {
            return (MirrorHandler<T>) NO_OP;
        }

        public static MirrorHandler<Direction> direction() {
            return DIRECTION;
        }

        private static final MirrorHandler<?> NO_OP = (mirror, value) -> value;
        private static final MirrorHandler<Direction> DIRECTION = Mirror::mirror;
    }
}
