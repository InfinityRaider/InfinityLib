package com.infinityraider.infinitylib.utility.inventory;

import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

/**
 * IInventory interface to automatically have methods implemented to read/write inventory data from/to nbt
 */
public interface IContainerSerializable extends IContainerWrapped {
    default CompoundTag writeInventoryToNBT(CompoundTag tag) {
        ListTag list = new ListTag();
        for(int i = 0; i < this.getContainerSize(); i++) {
            ItemStack stack = this.getItem(i);
            CompoundTag tagAt = stack.serializeNBT();
            boolean flag = !stack.isEmpty() && stack.getCount() > 0;
            tagAt.putBoolean(Names.NBT.FLAG, flag);
            if(flag) {
                stack.save(tagAt);
            }
            list.add(tagAt);
        }
        tag.put(Names.NBT.LIST, list);
        return tag;
    }

    default CompoundTag readInventoryFromNBT(CompoundTag tag) {
        if(tag.contains(Names.NBT.LIST)) {
            ListTag list = tag.getList(Names.NBT.LIST, 10);
            for(int i = 0; i < this.getContainerSize(); i++) {
                CompoundTag tagAt = list.getCompound(i);
                if(tagAt.getBoolean(Names.NBT.FLAG)) {
                    ItemStack stack = ItemStack.of(tagAt);
                    this.setItem(i, stack);
                } else {
                    this.setItem(i, ItemStack.EMPTY);
                }
            }
        }
        return tag;
    }
}
