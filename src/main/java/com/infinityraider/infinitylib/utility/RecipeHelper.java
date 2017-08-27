package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.InfinityMod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import java.util.Iterator;

public abstract class RecipeHelper {
    public static void registerRecipes(InfinityMod mod) {
        //blocks
        ReflectionHelper.forEachIn(mod.getModBlockRegistry(), IRecipeRegister.class, IRecipeRegister::registerRecipes);
        //items
        ReflectionHelper.forEachIn(mod.getModItemRegistry(), IRecipeRegister.class, IRecipeRegister::registerRecipes);
    }

    public static void removeRecipe(ItemStack stack) {
        Iterator<IRecipe> iterator = CraftingManager.REGISTRY.iterator();
        while(iterator.hasNext()) {
            IRecipe recipe = iterator.next();
            ItemStack result = recipe.getRecipeOutput();
            if(result.getItem().equals(stack.getItem()) && result.getItemDamage() == stack.getItemDamage()) {
                iterator.remove();
            }
        }
    }

    /*
    public static SoundEvent registerSound(String modId, String name) {
        return registerSound(new ResourceLocation(modId, name));
    }

    public static SoundEvent registerSound(ResourceLocation id) {
        int size = SoundEvent.REGISTRY.getKeys().size();
        SoundEvent sound = new SoundEvent(id);
        SoundEvent.REGISTRY.register(size, id, sound);
        return sound;
    }
    */
}
