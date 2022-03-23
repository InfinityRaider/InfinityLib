package com.infinityraider.infinitylib.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ContainerMenuBase extends AbstractContainerMenu {

    public static final int PLAYER_INVENTORY_SIZE = 36;

    public ContainerMenuBase(@Nullable MenuType<?> type, int id, Inventory inventory, int xOffset, int yOffset) {
        super(type, id);
        // Add the player's main inventory to the container.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                //new Slot(inventory, slot index, x coordinate, y coordinate)
                this.addSlot(new Slot(inventory, j + i * 9 + 9, xOffset + j * 18, yOffset + i * 18));
            }
        }

        // Add the player's hotbar inventory to the container.
        for (int i = 0; i < 9; i++) {
            //new Slot(inventory, slot index, x coordinate, y coordinate)
            this.addSlot(new Slot(inventory, i, xOffset + i * 18, 58 + yOffset));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int start, int stop, boolean backwards) {
        boolean foundSlot = false;
        int slotIndex = backwards ? stop - 1 : start;
        Slot slot;
        ItemStack stackInSlot;
        //try to stack with existing stacks first
        if (stack.isStackable()) {
            while (stack.getCount() > 0 && (!backwards && slotIndex < stop || backwards && slotIndex >= start)) {
                slot = this.slots.get(slotIndex);
                stackInSlot = slot.getItem();
                if (!(stackInSlot.isEmpty()) && slot.mayPlace(stack) && stackInSlot.getItem() == stack.getItem() && ItemStack.tagMatches(stack, stackInSlot)) {
                    int combinedSize = stackInSlot.getCount() + stack.getCount();
                    if (combinedSize <= stack.getMaxStackSize()) {
                        stack.setCount(0);
                        stackInSlot.setCount(combinedSize);
                        slot.setChanged();
                        foundSlot = true;
                    } else if (stackInSlot.getCount() < stack.getMaxStackSize()) {
                        stack.setCount(stack.getCount() - (stack.getMaxStackSize() - stackInSlot.getCount()));
                        stackInSlot.setCount(stack.getMaxStackSize());
                        slot.setChanged();
                        foundSlot = true;
                    }
                }
                slotIndex = backwards ? slotIndex - 1 : slotIndex + 1;
            }
        }
        //put in empty slot
        if (stack.getCount() > 0) {
            slotIndex = backwards ? stop - 1 : start;
            while (!backwards && slotIndex < stop || backwards && slotIndex >= start && !foundSlot) {
                slot = this.slots.get(slotIndex);
                stackInSlot = slot.getItem();
                if (stackInSlot.isEmpty() && slot.mayPlace(stack)) {
                    slot.set(stack.copy());
                    slot.setChanged();
                    stack.setCount(0);
                    foundSlot = true;
                    break;
                }
                slotIndex = backwards ? slotIndex - 1 : slotIndex + 1;
            }
        }
        return foundSlot;
    }

}
