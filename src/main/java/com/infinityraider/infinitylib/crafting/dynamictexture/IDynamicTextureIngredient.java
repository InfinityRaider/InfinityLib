package com.infinityraider.infinitylib.crafting.dynamictexture;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.tags.ITag;

public interface IDynamicTextureIngredient {
    ResourceLocation getTagId();

    ITag<Block> getTag();

    default ItemStack asStackWithMaterial(Block block) {
        return this.asStackWithMaterial(new ItemStack(block));
    }

    ItemStack asStackWithMaterial(ItemStack material);
}
