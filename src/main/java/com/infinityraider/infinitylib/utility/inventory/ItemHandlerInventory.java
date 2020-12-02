package com.infinityraider.infinitylib.utility.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Simple class to wrap an IInventory as an IItemHandler while maintaining IInventory functionality
 */
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

    @Nullable
    @Override
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

    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return  this.getInventory().decrStackSize(index, count);
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return  this.getInventory().removeStackFromSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
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
    public boolean isUsableByPlayer(PlayerEntity player) {
        return this.getInventory().isUsableByPlayer(player);
    }

    @Override
    public void openInventory(PlayerEntity player) {
        this.getInventory().openInventory(player);
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        this.getInventory().closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return this.getInventory().isItemValidForSlot(index, stack);
    }

    @Override
    public void clear() {
        this.getInventory().clear();
    }
}
