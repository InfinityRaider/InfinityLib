package com.infinityraider.infinitylib.crafting.dynamictexture;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShapelessDynamicTextureRecipe extends ShapelessRecipe {
    public static final String ID = "crafting_shapeless_dynamic_texture";
    public static final IRecipeSerializer<ShapelessDynamicTextureRecipe> SERIALIZER = new Serializer();

    public ShapelessDynamicTextureRecipe(ShapelessRecipe parent) {
        super(parent.getId(), parent.getGroup(), parent.getRecipeOutput(), parent.getIngredients());
    }

    @Override
    @Nonnull
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
        // TODO
        return super.matches(inv, world);
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
        // TODO
        return super.getCraftingResult(inv);
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<ShapelessDynamicTextureRecipe>, IInfRecipeSerializer {

        private Serializer() {
        }

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
        public ShapelessDynamicTextureRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            return this.convert(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, json));
        }

        @Nullable
        @Override
        public ShapelessDynamicTextureRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            return this.convert(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, buffer));
        }

        protected ShapelessDynamicTextureRecipe convert(@Nullable ShapelessRecipe recipe) {
            return recipe == null ? null : new ShapelessDynamicTextureRecipe(recipe);
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, @Nonnull ShapelessDynamicTextureRecipe recipe) {
            IRecipeSerializer.CRAFTING_SHAPELESS.write(buffer, recipe);
        }

        @Override
        public ImmutableList<IInfIngredientSerializer<?>> getIngredientSerializers() {
            return ImmutableList.of(DynamicTextureIngredient.SERIALIZER);
        }
    }
}
