package com.infinityraider.infinitylib.crafting.dynamictexture;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public interface IDynamicTextureIngredient {
    ResourceLocation getTagId();

    ITag<Block> getTag();

    default ItemStack asStackWithMaterial(Block block) {
        return this.asStackWithMaterial(new ItemStack(block));
    }

    ItemStack asStackWithMaterial(ItemStack material);
}
