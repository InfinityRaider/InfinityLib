package com.infinityraider.infinitylib.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.Objects;

public class ContainerTileBase<T extends TileEntity> extends ContainerBase {

    private final T tile;

    public ContainerTileBase(@Nullable ContainerType<?> type, int id, T tile, PlayerInventory inventory, int xOffset, int yOffset) {
        super(type, id, inventory, xOffset, yOffset);
        // Set the TileEntity associated with the container.
        this.tile = Objects.requireNonNull(tile, "The TileEntity associated with a TileEntity Container may not be null!");
    }

    /**
     * Fetches the TileEntity associated with this container.
     *
     * @return The TileEntity associated with this container.
     */
    public T getTile() {
        return this.tile;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return !getTile().isRemoved();
    }
}
