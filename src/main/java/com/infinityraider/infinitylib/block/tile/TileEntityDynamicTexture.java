package com.infinityraider.infinitylib.block.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;

public abstract class TileEntityDynamicTexture extends TileEntityBase {
    private final AutoSyncedField<ItemStack> material;

    public TileEntityDynamicTexture(TileEntityType<?> type) {
        super(type);
        this.material = this.createField(ItemStack.EMPTY, ItemStack::write, ItemStack::read);
    }

    public ItemStack getMaterial() {
        return this.material.get();
    }

    public void setMaterial(ItemStack material) {
        this.material.set(material);
    }
}
