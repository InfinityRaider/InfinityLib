package com.infinityraider.infinitylib.utility.text;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class ItemStackParser {
    /**
     * parses a string into an item stack.
     * String format is domain:item_id:metadata.
     * Metadata is optional, if no metadata is specified, the ItemStack will have OreDictionary.WILDCARD_VALUE as metadata
     *
     * @param string the string
     * @return the parsed ItemStack
     */
    public static Optional<ItemStack> parseItemStack(String string) {
        String[] split = string.split(":");
        if(split.length <= 2) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(string));
            return item == null ? Optional.empty() : Optional.of(new ItemStack(item, 1));
        } else {
            //TODO: metadata
            /*
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(split[0] + ":" + split[1]));
            if(item == null) {
                return Optional.empty();
            }
            int meta = -1;
            try {
                meta = Integer.parseInt(split[2]);
            } catch(Exception e) {
                InfinityLib.instance.getLogger().info("[ERROR] Failed parsing of item metadata for " + string);
                InfinityLib.instance.getLogger().printStackTrace(e);
            }
            return meta < 0 ? Optional.empty() : Optional.of(new ItemStack(item, 1, meta));
             */
            return Optional.empty();
        }
    }
}
