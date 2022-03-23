package com.infinityraider.infinitylib.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Objects;

public class ContainerTileBase<T extends BlockEntity> extends ContainerBase {

    private final T tile;

    public ContainerTileBase(@Nullable MenuType<?> type, int id, T tile, Inventory inventory, int xOffset, int yOffset) {
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
    public boolean canInteractWith(Player player) {
        return !getTile().isRemoved();
    }
}
