package com.infinityraider.infinitylib.utility.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Simple class to wrap an IInventory as an IItemHandler while maintaining IInventory functionality
 */
@SuppressWarnings("unused")
public class ItemHandlerInventory implements IInventoryItemHandler {
    private final IInventory inventory;

    public ItemHandlerInventory(IInventory inventory) {
        this.inventory = inventory;
    }

    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public int getSizeInventory() {
        return this.getInventory().getSizeInventory();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int index) {
        return  this.getInventory().getStackInSlot(index);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.getInventory().getInventoryStackLimit();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.getInventory().isItemValidForSlot(slot, stack);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int index, int count) {
        return  this.getInventory().decrStackSize(index, count);
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int index) {
        return  this.getInventory().removeStackFromSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        this.getInventory().setInventorySlotContents(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return this.getInventory().getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
        this.getInventory().markDirty();
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull PlayerEntity player) {
        return this.getInventory().isUsableByPlayer(player);
    }

    @Override
    public void openInventory(@Nonnull PlayerEntity player) {
        this.getInventory().openInventory(player);
    }

    @Override
    public void closeInventory(@Nonnull PlayerEntity player) {
        this.getInventory().closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        return this.getInventory().isItemValidForSlot(index, stack);
    }

    @Override
    public void clear() {
        this.getInventory().clear();
    }
}
