package com.infinityraider.infinitylib.block.tile;

import net.minecraft.util.Direction;

@SuppressWarnings("unused")
public interface IRotatableTile {
    Direction getOrientation();

    void setOrientation(Direction facing);

    void incrementRotation(int amount);
}
