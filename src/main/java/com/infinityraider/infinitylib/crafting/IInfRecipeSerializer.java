package com.infinityraider.infinitylib.crafting;

import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Collection;

public interface IInfRecipeSerializer extends IInfinityRegistrable<RecipeSerializer<?>> {
    Collection<IInfIngredientSerializer<?>> getIngredientSerializers();
}
