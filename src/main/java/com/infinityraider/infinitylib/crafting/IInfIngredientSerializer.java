package com.infinityraider.infinitylib.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public interface IInfIngredientSerializer<T extends Ingredient> extends IIngredientSerializer<T> {
    ResourceLocation getId();
}
