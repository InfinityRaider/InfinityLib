package com.infinityraider.infinitylib.utility.inventory;

import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * IInventory shares an MCP-mapped method with IItemHandler: getStackInSlot(int index)
 * Classes implementing both interfaces will have the implementation of this method obfuscated for IInventory,
 * effectively removing also the IItemHandler implementation.
 *
 * This will lead to AbstractMethodExceptions, therefore this wrapper interface has been created.
 *
 * It is ugly, I know
 */
public interface IWorldlyContainerWrapped extends WorldlyContainer {
    @Nonnull
    @Override
    default ItemStack getItem(int index) {
        return this.getStackInInvSlot(index);
    }

    ItemStack getStackInInvSlot(int index);
}
