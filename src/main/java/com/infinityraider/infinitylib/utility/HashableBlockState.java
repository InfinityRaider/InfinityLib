package com.infinityraider.infinitylib.utility;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HashableBlockState {

    @Nonnull
    private final Block block;
    @Nullable
    private final Direction side;
    @Nonnull
    private final Collection<Property<?>> properties;

    public HashableBlockState(@Nonnull BlockState state) {
        this(state, null);
    }

    public HashableBlockState(@Nonnull BlockState state, @Nullable Direction side) {
        this(
                state.getBlock(),
                state.getProperties(),
                side
        );
    }

    public HashableBlockState(
            @Nonnull Block block,
            @Nonnull Collection<Property<?>> properties,
            @Nullable Direction side
    ) {
        this.block = Preconditions.checkNotNull(block);
        this.properties = Preconditions.checkNotNull(properties);
        this.side = side;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HashableBlockState) {
            final HashableBlockState other = (HashableBlockState) obj;
            return Objects.equals(this.block, other.block)
                    && Objects.equals(this.side, other.side)
                    && Objects.equals(this.properties, other.properties);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(block);
        hash = 31 * hash + Objects.hashCode(side);
        hash = 31 * hash + Objects.hashCode(properties);
        return hash;
    }

}
