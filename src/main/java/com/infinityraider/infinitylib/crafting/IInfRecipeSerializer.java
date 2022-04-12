package com.infinityraider.infinitylib.crafting;

import com.infinityraider.infinitylib.utility.registration.IInfinityRegistrable;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Collection;

public interface IInfRecipeSerializer<T extends Recipe<?>> extends IInfinityRegistrable<RecipeSerializer<?>>, RecipeSerializer<T> {
    Collection<IInfIngredientSerializer<?>> getIngredientSerializers();
}
