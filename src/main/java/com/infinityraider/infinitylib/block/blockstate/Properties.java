package com.infinityraider.infinitylib.block.blockstate;

import net.minecraft.block.properties.PropertyInteger;

public class Properties {
    public static InfinityProperty<Integer> DIRECTIONS = new InfinityProperty<>(PropertyInteger.create("direction", 0, 3), 0);
}
