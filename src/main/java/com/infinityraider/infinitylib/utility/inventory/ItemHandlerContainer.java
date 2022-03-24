package com.infinityraider.infinitylib.utility.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Simple class to wrap an IInventory as an IItemHandler while maintaining IInventory functionality
 */
@SuppressWarnings("unused")
public class ItemHandlerContainer implements IContainerItemHandler {
    private final Container inventory;

    public ItemHandlerContainer(Container inventory) {
        this.inventory = inventory;
    }

    public Container getInventory() {
        return inventory;
    }

    @Override
    public int getContainerSize() {
        return this.getInventory().getContainerSize();
    }

    @Override
    @Nonnull
    public ItemStack getStackInInvSlot(int index) {
        return  this.getInventory().getItem(index);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.getInventory().getMaxStackSize();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.getInventory().canPlaceItem(slot, stack);
    }

    @Override
    @Nonnull
    public ItemStack removeItem(int index, int count) {
        return  this.getInventory().removeItem(index, count);
    }

    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate(int index) {
        return  this.getInventory().removeItemNoUpdate(index);
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        this.getInventory().setItem(index, stack);
    }

    @Override
    public int getMaxStackSize() {
        return this.getInventory().getMaxStackSize();
    }

    @Override
    public void setChanged() {
        this.getInventory().setChanged();
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return this.getInventory().stillValid(player);
    }

    @Override
    public void startOpen(@Nonnull Player player) {
        this.getInventory().startOpen(player);
    }

    @Override
    public void stopOpen(@Nonnull Player player) {
        this.getInventory().stopOpen(player);
    }

    @Override
    public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
        return this.getInventory().canPlaceItem(index, stack);
    }

    @Override
    public void clearContent() {
        this.getInventory().clearContent();
    }
}
