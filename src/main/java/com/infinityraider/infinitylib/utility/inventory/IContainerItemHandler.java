package com.infinityraider.infinitylib.utility.inventory;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * IInventory interface to link the IItemHandler methods automatically to the Container methods
 */
public interface IContainerItemHandler extends IContainerWrapped, IItemHandlerWrapped {
    @Nonnull
    @Override
    default ItemStack getItem(int index) {
        return IItemHandlerWrapped.super.getStackInSlot(index);
    }

    @Override
    default int getSlots() {
        return this.getContainerSize();
    }

    @Override
    default int getSlotLimit(int slot) {
        return this.getMaxStackSize();
    }

    @Override
    @Nonnull
    default ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(!isValidSlot(slot) || stack.isEmpty() || stack.getCount() <= 0 || !this.canPlaceItem(slot, stack)) {
            return stack;
        }
        ItemStack inSlot = this.getStackInSlot(slot);
        if(inSlot.isEmpty() || inSlot.getCount() <= 0) {
            if(!simulate) {
                this.setItem(slot, stack.copy());
            }
            return ItemStack.EMPTY;
        } else if(ItemStack.matches(inSlot, stack) && ItemStack.tagMatches(inSlot, stack)) {
            int max = stack.getItem().getItemStackLimit(stack);
            int transfer = Math.min(stack.getCount(), max - inSlot.getCount());
            if(!simulate) {
                inSlot.setCount(inSlot.getCount() + transfer);
                this.setItem(slot, inSlot);
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
                    this.setItem(slot, ItemStack.EMPTY);
                }
                return stack;
            } else {
                stack.setCount(amount);
                if(!simulate) {
                    inSlot.setCount(inSlot.getCount() - amount);
                    this.setItem(slot, inSlot);
                }
                return stack;
            }
        }
    }

    @Override
    default boolean isEmpty() {
        for(int i = 0; i < this.getContainerSize(); i++) {
            if(!this.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }



    @Override
    default boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.canPlaceItem(slot, stack);
    }

    default boolean isValidSlot(int slot) {
        return slot >= 0 && slot < this.getSlots();
    }
}
