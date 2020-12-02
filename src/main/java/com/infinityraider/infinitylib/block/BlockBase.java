package com.infinityraider.infinitylib.block;

import net.minecraft.block.Block;

import javax.annotation.Nonnull;

public abstract class BlockBase extends Block implements IInfinityBlock {
    private final String internalName;

    public BlockBase(String name, Properties properties) {
        super(properties);
        this.internalName = name;
    }

    public boolean isEnabled() {
        return true;
    }

    @Override
    @Nonnull
    public String getInternalName() {
        return this.internalName;
    }
}
