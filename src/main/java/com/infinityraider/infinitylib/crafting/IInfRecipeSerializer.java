package com.infinityraider.infinitylib.crafting;

import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import net.minecraft.item.crafting.IRecipeSerializer;

import java.util.Collection;

public interface IInfRecipeSerializer extends IInfinityRegistrable<IRecipeSerializer<?>> {
    Collection<IInfIngredientSerializer<?>> getIngredientSerializers();
}
