package com.infinityraider.infinitylib.utility.text;

import com.infinityraider.infinitylib.utility.LogHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

public class ItemStackParser {
    /**
     * parses a string into an item stack.
     * String format is domain:item_id:metadata.
     * Metadata is optional, if no metadata is specified, the ItemStack will have OreDictionary.WILDCARD_VALUE as metadata
     *
     * Can return null if no such item exists
     *
     * @param string the string
     * @return the parsed ItemStack
     */
    @Nullable
    public static ItemStack parseItemStack(String string) {
        String[] split = string.split(":");
        if(split.length <= 2) {
            Item item = Item.REGISTRY.getObject(new ResourceLocation(string));
            return item == null ? null : new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
        } else {
            Item item = Item.REGISTRY.getObject(new ResourceLocation(split[0] + ":" + split[1]));
            if(item == null) {
                return null;
            }
            int meta = -1;
            try {
                meta = Integer.parseInt(split[2]);
            } catch(Exception e) {
                LogHelper.info("[ERROR] Failed parsing of item metadata for " + string);
                LogHelper.printStackTrace(e);
            }
            return meta < 0 ? null : new ItemStack(item, 1, meta);
        }
    }
}
