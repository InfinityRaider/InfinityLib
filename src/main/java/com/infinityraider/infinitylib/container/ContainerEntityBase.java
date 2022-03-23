package com.infinityraider.infinitylib.container;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import java.util.Objects;

public class ContainerEntityBase<E extends Entity> extends ContainerBase {

    private final E entity;

    public ContainerEntityBase(@Nullable MenuType<?> type, int id, E entity, Inventory inventory, int xOffset, int yOffset) {
        super(type, id, inventory, xOffset, yOffset);
        // Set the Entity associated with the container.
        this.entity = Objects.requireNonNull(entity, "The Entity associated with an Entity Container may not be null!");
    }

    /**
     * Fetches the Entity associated with this container.
     *
     * @return The Entity associated with this container.
     */
    public E getEntity() {
        return this.entity;
    }

    @Override
    public boolean canInteractWith(Player player) {
        return this.getEntity().isAlive();
    }
}
