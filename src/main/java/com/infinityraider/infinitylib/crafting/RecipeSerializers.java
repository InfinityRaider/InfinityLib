package com.infinityraider.infinitylib.crafting;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.crafting.dynamictexture.ShapedDynamicTextureRecipe;
import com.infinityraider.infinitylib.crafting.dynamictexture.ShapelessDynamicTextureRecipe;
import com.infinityraider.infinitylib.crafting.fallback.FallbackIngredient;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.Map;

public class RecipeSerializers extends ModContentRegistry {
    private static final RecipeSerializers INSTANCE = new RecipeSerializers();

    public static RecipeSerializers getInstance() {
        return INSTANCE;
    }

    // all serializers
    private final Map<ResourceLocation, IInfIngredientSerializer<?>> ingredientSerializers = Maps.newConcurrentMap();

    // ingredients
    public final IInfIngredientSerializer<FallbackIngredient> fallbackIngredient = FallbackIngredient.SERIALIZER;

    // recipes
    public final RegistryInitializer<IInfRecipeSerializer<ShapedDynamicTextureRecipe>> shapedDynamicTextureRecipe;
    public final RegistryInitializer<IInfRecipeSerializer<ShapelessDynamicTextureRecipe>> shapelessDynamicTextureRecipe;

    private RecipeSerializers() {
        this.shapedDynamicTextureRecipe = this.recipe(() -> ShapedDynamicTextureRecipe.SERIALIZER);
        this.shapelessDynamicTextureRecipe = this.recipe(() -> ShapelessDynamicTextureRecipe.SERIALIZER);
    }

    public void registerSerializer(IInfIngredientSerializer<?> serializer) {
       ingredientSerializers.putIfAbsent(serializer.getId(), serializer);
    }

    public void registerSerializers() {
        ingredientSerializers.values().forEach(serializer -> CraftingHelper.register(serializer.getId(), serializer));
    }
}
