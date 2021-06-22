package com.infinityraider.infinitylib.utility.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

/**
 * Simple class to wrap an IItemHandler as an IInventory while maintaining IItemHandler functionality
 */
@SuppressWarnings("unused")
public class InventoryItemHandler implements IInventoryItemHandler {
    private final IItemHandler itemHandler;

    public InventoryItemHandler(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }

    /**
     * --------------------
     * IItemHandler methods
     * --------------------
     */

    @Override
    public int getSlots() {
        return this.getItemHandler().getSlots();
    }

    @Override
    @Nonnull
    public ItemStack getStackInInvSlot(int index) {
        return this.getItemHandler().getStackInSlot(index);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return this.getItemHandler().insertItem(slot, stack, simulate);
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.getItemHandler().extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.getItemHandler().getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.getItemHandler().isItemValid(slot, stack);
    }


    /**
     * ------------------
     * IInventory methods
     * ------------------
     */

    @Override
    public int getSizeInventory() {
        return this.getSlots();
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = this.getStackInSlot(index);
        if(!stack.isEmpty()) {
            stack = this.extractItem(index, count, false);
        }
        return stack;
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.getStackInSlot(index);
        if(!stack.isEmpty()) {
            stack = this.extractItem(index, stack.getCount(), false);
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        ItemStack inSlot = this.getStackInSlot(index);
        if(!inSlot.isEmpty()) {
            this.extractItem(index, inSlot.getCount(), false);
        }
        this.insertItem(index, stack, false);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUsableByPlayer(@Nonnull PlayerEntity player) {
        return true;
    }

    @Override
    public void openInventory(@Nonnull PlayerEntity player) {}

    @Override
    public void closeInventory(@Nonnull PlayerEntity player) {}

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        ItemStack simulated = this.insertItem(index, stack, true);
        return simulated.getCount() != stack.getCount();
    }

    @Override
    public void clear() {
        for(int i = 0; i < this.getSizeInventory(); i++) {
            this.setInventorySlotContents(i, ItemStack.EMPTY);
        }
    }
}
