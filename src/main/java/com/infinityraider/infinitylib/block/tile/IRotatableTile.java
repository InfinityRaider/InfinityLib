package com.infinityraider.infinitylib.block.tile;

import com.infinityraider.infinitylib.utility.math.Directions;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public interface IRotatableTile {
    EnumFacing getOrientation();

    void setOrientation(EnumFacing facing);

   Directions.Direction getDirection();

   void setDirection(Directions.Direction orientation);
}
