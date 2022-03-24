package com.infinityraider.infinitylib.utility.inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

/**
 * Simple class to wrap an IItemHandler as an IInventory while maintaining IItemHandler functionality
 */
@SuppressWarnings("unused")
public class ContainerItemHandler implements IContainerItemHandler {
    private final IItemHandler itemHandler;

    public ContainerItemHandler(IItemHandler itemHandler) {
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
     * Container methods
     * ------------------
     */

    @Override
    public int getContainerSize() {
        return this.getSlots();
    }

    @Override
    @Nonnull
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = this.getStackInSlot(index);
        if(!stack.isEmpty()) {
            stack = this.extractItem(index, count, false);
        }
        return stack;
    }

    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = this.getStackInSlot(index);
        if(!stack.isEmpty()) {
            stack = this.extractItem(index, stack.getCount(), false);
        }
        return stack;
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        ItemStack inSlot = this.getStackInSlot(index);
        if(!inSlot.isEmpty()) {
            this.extractItem(index, inSlot.getCount(), false);
        }
        this.insertItem(index, stack, false);
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Override
    public void startOpen(@Nonnull Player player) {}

    @Override
    public void stopOpen(@Nonnull Player player) {}

    @Override
    public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
        ItemStack simulated = this.insertItem(index, stack, true);
        return simulated.getCount() != stack.getCount();
    }

    @Override
    public void clearContent() {
        for(int i = 0; i < this.getContainerSize(); i++) {
            this.setItem(i, ItemStack.EMPTY);
        }
    }
}
