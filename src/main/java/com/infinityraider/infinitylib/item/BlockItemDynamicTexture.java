package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class BlockItemDynamicTexture extends BlockItemBase {
    public BlockItemDynamicTexture(BlockDynamicTexture<?> block, Properties properties) {
        super(block, properties);
    }

    public void setMaterial(ItemStack stack, ItemStack material) {
        CompoundNBT tag = stack.getTag();
        if(tag == null) {
            tag = new CompoundNBT();
            stack.setTag(tag);
        }
        tag.put(Names.NBT.MATERIAL, material.write(new CompoundNBT()));
    }

    public ItemStack getMaterial(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if(tag == null || !tag.contains(Names.NBT.MATERIAL)) {
            return ItemStack.EMPTY;
        }
        return ItemStack.read(tag.getCompound(Names.NBT.MATERIAL));
    }
}
