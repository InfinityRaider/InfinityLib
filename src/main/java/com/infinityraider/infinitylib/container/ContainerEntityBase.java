package com.infinityraider.infinitylib.container;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;

import javax.annotation.Nullable;
import java.util.Objects;

public class ContainerEntityBase<E extends Entity> extends ContainerBase {

    private final E entity;

    public ContainerEntityBase(@Nullable ContainerType<?> type, int id, E entity, PlayerInventory inventory, int xOffset, int yOffset) {
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
    public boolean canInteractWith(PlayerEntity player) {
        return this.getEntity().isAlive();
    }
}
