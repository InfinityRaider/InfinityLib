package com.infinityraider.infinitylib.reference;

public class Constants {
    /**
     * The number of units in a block.
     */
    public static final int WHOLE = 16;

    /**
     * The value of 1/16 as represented in float form. Pre-calculated as to cut
     * back on calculations.
     */
    public static final float UNIT = 1.0f / WHOLE;

    /**
     * The value of half a block. Pre-calculated as to cut
     * back on calculations.
     */
    public static final float HALF = UNIT * WHOLE / 2;

    /**
     * The value of quarter a block. Pre-calculated as to cut
     * back on calculations.
     */
    public static final float QUARTER = UNIT * WHOLE / 4;

    /**
     * The value of three quarter a block. Pre-calculated as to cut
     * back on calculations.
     */
    public static final float THREE_QUARTER = 3 * QUARTER;

    /**
     * The representation of 1 bucket(b) in millibuckets(mB).
     */
    int BUCKET_mB = 1000;

    /**
     * The representation of 1/2 a bucket(b) in millibuckets(mB).
     */
    int HALF_BUCKET_mB = BUCKET_mB / 2;

    /**
     * The representation of 1/4 a bucket(b) in millibuckets(mB).
     */
    int QUARTER_BUCKET_mB = BUCKET_mB / 4;
}
