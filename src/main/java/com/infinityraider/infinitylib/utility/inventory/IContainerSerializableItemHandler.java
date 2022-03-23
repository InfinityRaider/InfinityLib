package com.infinityraider.infinitylib.utility.inventory;

/**
 * IInventory interface which automatically has both IItemHandler functionality as well as nbt serialization
 */
public interface IContainerSerializableItemHandler extends IContainerSerializable, IContainerItemHandler {}
