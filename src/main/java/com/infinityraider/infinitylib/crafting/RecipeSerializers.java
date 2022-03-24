package com.infinityraider.infinitylib.crafting;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.crafting.dynamictexture.ShapedDynamicTextureRecipe;
import com.infinityraider.infinitylib.crafting.dynamictexture.ShapelessDynamicTextureRecipe;
import com.infinityraider.infinitylib.crafting.fallback.FallbackIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.Map;

public class RecipeSerializers {
    // all serializers
    private static final Map<ResourceLocation, IInfIngredientSerializer<?>> ingredientSerializers = Maps.newConcurrentMap();

    // ingredients
    public static final IInfIngredientSerializer<FallbackIngredient> fallbackIngredient = FallbackIngredient.SERIALIZER;

    // recipes
    public static final RecipeSerializer<ShapedDynamicTextureRecipe> shapedDynamicTextureRecipe = ShapedDynamicTextureRecipe.SERIALIZER;
    public static final RecipeSerializer<ShapelessDynamicTextureRecipe> shapelessDynamicTextureRecipe = ShapelessDynamicTextureRecipe.SERIALIZER;

    public static void registerSerializer(IInfIngredientSerializer<?> serializer) {
       ingredientSerializers.putIfAbsent(serializer.getId(), serializer);
    }

    public static void registerSerializers() {
        ingredientSerializers.values().forEach(serializer -> CraftingHelper.register(serializer.getId(), serializer));
    }
}
