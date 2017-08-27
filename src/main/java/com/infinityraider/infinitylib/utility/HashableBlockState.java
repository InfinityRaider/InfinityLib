/*
 */
package com.infinityraider.infinitylib.utility;

import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 *
 */
public class HashableBlockState {

    private final Block block;
    private final ImmutableMap<IProperty<?>, Comparable<?>> listedProperties;
    private final ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties;

    public HashableBlockState(IBlockState state) {
        this(
                state.getBlock(),
                state.getProperties(),
                (state instanceof IExtendedBlockState) ? ((IExtendedBlockState) state).getUnlistedProperties() : null
        );
    }

    public HashableBlockState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> listedProperties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
        this.block = block;
        this.listedProperties = listedProperties;
        this.unlistedProperties = unlistedProperties;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HashableBlockState) {
            final HashableBlockState other = (HashableBlockState) obj;
            return Objects.equals(this.block, other.block)
                    && Objects.equals(this.listedProperties, other.listedProperties)
                    && Objects.equals(this.unlistedProperties, other.unlistedProperties);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(block);
        hash = 31 * hash + Objects.hashCode(listedProperties);
        hash = 31 * hash + Objects.hashCode(unlistedProperties);
        return hash;
    }

}
