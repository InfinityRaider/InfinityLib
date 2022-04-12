package com.infinityraider.infinitylib.crafting.dynamictexture;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShapelessDynamicTextureRecipe extends ShapelessRecipe {
    public static final String ID = "crafting_shapeless_dynamic_texture";
    public static final IInfRecipeSerializer<ShapelessDynamicTextureRecipe> SERIALIZER = new Serializer();

    public ShapelessDynamicTextureRecipe(ShapelessRecipe parent) {
        super(parent.getId(), parent.getGroup(), parent.getResultItem(), parent.getIngredients());
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level world) {
        // TODO
        return super.matches(inv, world);
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull CraftingContainer inv) {
        // TODO
        return super.assemble(inv);
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements IInfRecipeSerializer<ShapelessDynamicTextureRecipe> {
        private Serializer() {}

        @Nonnull
        @Override
        public String getInternalName() {
            return ID;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Nonnull
        @Override
        public ShapelessDynamicTextureRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            return this.convert(RecipeSerializer.SHAPELESS_RECIPE.fromJson(recipeId, json));
        }

        @Nullable
        @Override
        public ShapelessDynamicTextureRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
            return this.convert(RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(recipeId, buffer));
        }

        protected ShapelessDynamicTextureRecipe convert(@Nullable ShapelessRecipe recipe) {
            return recipe == null ? null : new ShapelessDynamicTextureRecipe(recipe);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull ShapelessDynamicTextureRecipe recipe) {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
        }

        @Override
        public ImmutableList<IInfIngredientSerializer<?>> getIngredientSerializers() {
            return ImmutableList.of(
                    DynamicTextureIngredient.SERIALIZER,
                    DynamicTextureParentIngredient.SERIALIZER
            );
        }
    }
}
