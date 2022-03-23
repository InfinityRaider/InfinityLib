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
    private static final RecipeSerializers INSTANCE = new RecipeSerializers();

    public static RecipeSerializers getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, IInfIngredientSerializer<?>> ingredientSerializers;

    public final IInfIngredientSerializer<FallbackIngredient> fallbackIngredient;

    public final RecipeSerializer<ShapedDynamicTextureRecipe> shapedDynamicTextureRecipe;
    public final RecipeSerializer<ShapelessDynamicTextureRecipe> shapelessDynamicTextureRecipe;

    private RecipeSerializers() {
        // ingredients
        this.ingredientSerializers = Maps.newConcurrentMap();
        this.fallbackIngredient = FallbackIngredient.SERIALIZER;
        // recipes
        this.shapedDynamicTextureRecipe = ShapedDynamicTextureRecipe.SERIALIZER;
        this.shapelessDynamicTextureRecipe = ShapelessDynamicTextureRecipe.SERIALIZER;
    }

    public void registerSerializer(IInfIngredientSerializer<?> serializer) {
       this.ingredientSerializers.putIfAbsent(serializer.getId(), serializer);
    }

    public void registerSerializers() {
        this.ingredientSerializers.values().forEach(serializer -> CraftingHelper.register(serializer.getId(), serializer));
    }
}
