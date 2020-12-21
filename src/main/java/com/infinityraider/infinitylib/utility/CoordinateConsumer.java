package com.infinityraider.infinitylib.utility;

@FunctionalInterface
public interface CoordinateConsumer {
    void accept(double x, double y, double z);
}
