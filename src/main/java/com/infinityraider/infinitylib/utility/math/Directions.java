package com.infinityraider.infinitylib.utility.math;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public class Directions {
    public enum Axis implements IStringSerializable {
        X, Y, Z;

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

    }

    public enum AxisPosition implements IStringSerializable {

        X_NEG, X_MID, X_POS,
        Y_NEG, Y_MID, Y_POS,
        Z_NEG, Z_MID, Z_POS;

        static AxisPosition[][] resolved = new AxisPosition[][]{
                {X_NEG, X_MID, X_POS},
                {Y_NEG, Y_MID, Y_POS},
                {Z_NEG, Z_MID, Z_POS}
        };

        public static AxisPosition convert(Axis axis, int offset) {
            return resolved[axis.ordinal()][offset % 3];
        }

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

    }

    public enum Direction {
        /** -Y */
        DOWN(0, -1, 0, EnumFacing.DOWN, Axis.Y),

        /** +Y */
        UP(0, 1, 0, EnumFacing.UP, Axis.Y),

        /** -Z */
        NORTH(0, 0, -1, EnumFacing.NORTH, Axis.Z),

        /** +Z */
        SOUTH(0, 0, 1, EnumFacing.SOUTH,Axis.Z),

        /** -X */
        WEST(-1, 0, 0, EnumFacing.WEST, Axis.X),

        /** +X */
        EAST(1, 0, 0, EnumFacing.EAST, Axis.X),

        /**
         * Used only by getOrientation, for invalid inputs
         */
        UNKNOWN(0, 0, 0, null, Axis.X);


        public final Axis axis;
        public final int offsetX;
        public final int offsetY;
        public final int offsetZ;
        private final EnumFacing enumFacing;
        public final int flag;
        public static final Direction[] VALID_DIRECTIONS = {DOWN, UP, NORTH, SOUTH, WEST, EAST};
        public static final int[] OPPOSITES = {1, 0, 3, 2, 5, 4, 6};
        public static final int[] ORDINALS = {2, 4, 3, 5};
        // Left hand rule rotation matrix for all possible axes of rotation
        public static final int[][] ROTATION_MATRIX = {
                {0, 1, 4, 5, 3, 2, 6},
                {0, 1, 5, 4, 2, 3, 6},
                {5, 4, 2, 3, 0, 1, 6},
                {4, 5, 2, 3, 1, 0, 6},
                {2, 3, 1, 0, 4, 5, 6},
                {3, 2, 0, 1, 4, 5, 6},
                {0, 1, 2, 3, 4, 5, 6},
        };

        Direction(int x, int y, int z, EnumFacing enumFacing, Axis axis) {
            offsetX = x;
            offsetY = y;
            offsetZ = z;
            this.enumFacing = enumFacing;
            flag = 1 << ordinal();
            this.axis = axis;
        }

        public static Direction getOrientation(int id) {
            if (id >= 0 && id < VALID_DIRECTIONS.length) {
                return VALID_DIRECTIONS[id];
            }
            return UNKNOWN;
        }

        public static Direction getCardinal(int id) {
            return VALID_DIRECTIONS[ORDINALS[(id < 0 ? -id : id) % 4]];
        }

        public Direction getOpposite() {
            return getOrientation(OPPOSITES[ordinal()]);
        }

        public Direction getRotation(Direction axis){
            return getOrientation(ROTATION_MATRIX[axis.ordinal()][ordinal()]);
        }

        public EnumFacing getEnumFacing() {
            return enumFacing;
        }

        public static Direction getFromEnumFacing(EnumFacing facing) {
            if(facing == null) {
                return UNKNOWN;
            }
            return values()[facing.ordinal()];
        }

        public BlockPos offset(BlockPos pos) {
            return pos.add(this.offsetX, this.offsetY, this.offsetZ);
        }
    }

}
