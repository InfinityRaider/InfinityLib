package com.infinityraider.infinitylib.utility;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class RegisterHelper {
    public static void registerBlock(Block block, String modId, String name) {
        RegisterHelper.registerBlock(block, modId, name, null);
    }

    public static void registerBlock(Block block, String modId, String name, Class<? extends ItemBlock> itemClass) {
        block.setUnlocalizedName(modId.toLowerCase() + ':' + name);
        if (itemClass != null) {
            GameRegistry.registerBlock(block, itemClass, name);
        } else {
            GameRegistry.registerBlock(block, name);
        }
    }


    public static void registerItem(Item item, String modId, String name) {
        item.setUnlocalizedName(modId.toLowerCase()+':'+name);
        GameRegistry.registerItem(item, name);
    }
}
