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
        String unlocalized = modId.toLowerCase() + ':' + name;
        block.setUnlocalizedName(unlocalized);
        if (itemClass != null) {
            GameRegistry.registerBlock(block, unlocalized);
        } else {
            GameRegistry.registerBlock(block, unlocalized);
        }
    }


    public static void registerItem(Item item, String modId, String name) {
        String unlocalized = modId.toLowerCase() + ':' + name;
        item.setUnlocalizedName(unlocalized);
        GameRegistry.registerItem(item, unlocalized);
    }
}
