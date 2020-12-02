package com.infinityraider.infinitylib.utility.inventory;

import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

/**
 * IInventory interface to automatically have methods implemented to read/write inventory data from/to nbt
 */
public interface IInventorySerializable extends IInventory {
    default CompoundNBT writeInventoryToNBT(CompoundNBT tag) {
        ListNBT list = new ListNBT();
        for(int i = 0; i < this.getSizeInventory(); i++) {
            CompoundNBT tagAt = new CompoundNBT();
            ItemStack stack = this.getStackInSlot(i);
            boolean flag = !stack.isEmpty() && stack.getCount() > 0;
            tagAt.putBoolean(Names.NBT.FLAG, flag);
            if(flag) {
                stack.deserializeNBT(tagAt);
            }
            list.add(tagAt);
        }
        tag.put(Names.NBT.LIST, list);
        return tag;
    }

    default CompoundNBT readInventoryFromNBT(CompoundNBT tag) {
        if(tag.contains(Names.NBT.LIST)) {
            ListNBT list = tag.getList(Names.NBT.LIST, 10);
            for(int i = 0; i < this.getSizeInventory(); i++) {
                CompoundNBT tagAt = list.getCompound(i);
                if(tagAt.getBoolean(Names.NBT.FLAG)) {
                    ItemStack stack = ItemStack.read(tagAt);
                    this.setInventorySlotContents(i, stack);
                } else {
                    this.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
        }
        return tag;
    }
}
