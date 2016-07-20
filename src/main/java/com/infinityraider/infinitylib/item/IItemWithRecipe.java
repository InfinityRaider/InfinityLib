package com.infinityraider.infinitylib.item;

import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * Interface used to ease recipe registering
 */
public interface IItemWithRecipe {
    /**
     * @return a list of all recipes to be registered
     */
    List<IRecipe> getRecipes();
}
