package com.infinityraider.infinitylib.crafting;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public interface IInfIngredientSerializer<T extends Ingredient> extends IIngredientSerializer<T> {
    ResourceLocation getId();
}
