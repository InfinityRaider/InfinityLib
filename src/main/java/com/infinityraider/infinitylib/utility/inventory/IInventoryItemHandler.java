package com.infinityraider.infinitylib.utility.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * IInventory interface to link the IItemHandler methods automatically to the IInventory methods
 */
public interface IInventoryItemHandler extends IInventoryWrapped, IItemHandlerWrapped {
    @Nonnull
    @Override
    default ItemStack getStackInSlot(int index) {
        return IItemHandlerWrapped.super.getStackInSlot(index);
    }

    @Override
    default int getSlots() {
        return this.getSizeInventory();
    }

    @Override
    default int getSlotLimit(int slot) {
        return this.getInventoryStackLimit();
    }

    @Override
    @Nonnull
    default ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(!isValidSlot(slot) || stack.isEmpty() || stack.getCount() <= 0) {
            return stack;
        }
        ItemStack inSlot = this.getStackInSlot(slot);
        if(inSlot.isEmpty() || inSlot.getCount() <= 0) {
            if(!simulate) {
                this.setInventorySlotContents(slot, stack.copy());
            }
            return ItemStack.EMPTY;
        } else if(ItemStack.areItemsEqual(inSlot, stack) && ItemStack.areItemStackTagsEqual(inSlot, stack)) {
            int max = stack.getItem().getItemStackLimit(stack);
            int transfer = Math.min(stack.getCount(), max - inSlot.getCount());
            if(!simulate) {
                inSlot.setCount(inSlot.getCount() + transfer);
                this.setInventorySlotContents(slot, inSlot);
            }
            if(transfer >= stack.getCount()) {
                return ItemStack.EMPTY;
            } else {
                stack.setCount(stack.getCount() - transfer);
                return stack;
            }
        } else {
            return stack;
        }
    }

    @Override
    @Nonnull
    default ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(!isValidSlot(slot) || amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack inSlot = this.getStackInSlot(slot);
        if(inSlot.isEmpty() || inSlot.getCount() < 0) {
            return ItemStack.EMPTY;
        } else {
            amount = Math.min(amount, inSlot.getItem().getItemStackLimit(inSlot));
            ItemStack stack = inSlot.copy();
            if(amount >= inSlot.getCount()) {
                if(!simulate) {
                    this.setInventorySlotContents(slot, ItemStack.EMPTY);
                }
                return stack;
            } else {
                stack.setCount(amount);
                if(!simulate) {
                    inSlot.setCount(inSlot.getCount() - amount);
                    this.setInventorySlotContents(slot, inSlot);
                }
                return stack;
            }
        }
    }

    @Override
    default boolean isEmpty() {
        for(int i = 0; i < this.getSizeInventory(); i++) {
            if(!this.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }



    @Override
    default boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.isItemValidForSlot(slot, stack);
    }

    default boolean isValidSlot(int slot) {
        return slot >= 0 && slot < this.getSlots();
    }
}
