package com.infinityraider.infinitylib.utility.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * IInventory interface to link the IItemHandler methods automatically to the IInventory methods
 */
public interface IInventoryItemHandler extends IInventory, IItemHandler {
    @Override
    default int getSlots() {
        return this.getSizeInventory();
    }

    @Override
    default ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if(!isValidSlot(slot) || stack == null || stack.stackSize <= 0) {
            return null;
        }
        ItemStack inSlot = this.getStackInSlot(slot);
        if(inSlot == null || inSlot.stackSize <= 0) {
            if(!simulate) {
                this.setInventorySlotContents(slot, stack.copy());
            }
            return null;
        } else if(ItemStack.areItemsEqual(inSlot, stack) && ItemStack.areItemStackTagsEqual(inSlot, stack)) {
            int max = stack.getItem().getItemStackLimit(stack);
            int transfer = Math.min(stack.stackSize, max - inSlot.stackSize);
            if(!simulate) {
                inSlot.stackSize = inSlot.stackSize + transfer;
                this.setInventorySlotContents(slot, inSlot);
            }
            if(transfer >= stack.stackSize) {
                return null;
            } else {
                stack.stackSize = stack.stackSize - transfer;
                return stack;
            }
        } else {
            return stack;
        }
    }

    @Override
    default ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(!isValidSlot(slot) || amount <= 0) {
            return null;
        }
        ItemStack inSlot = this.getStackInSlot(slot);
        if(inSlot == null || inSlot.stackSize < 0) {
            return null;
        } else {
            amount = Math.min(amount, inSlot.getItem().getItemStackLimit(inSlot));
            ItemStack stack = inSlot.copy();
            if(amount >= inSlot.stackSize) {
                if(!simulate) {
                    this.setInventorySlotContents(slot, null);
                }
                return stack;
            } else {
                stack.stackSize = amount;
                if(!simulate) {
                    inSlot.stackSize = inSlot.stackSize - amount;
                    this.setInventorySlotContents(slot, inSlot);
                }
                return stack;
            }
        }
    }

    default boolean isValidSlot(int slot) {
        return slot >= 0 && slot < this.getSlots();
    }
}
