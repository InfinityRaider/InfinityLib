package com.infinityraider.infinitylib.crafting.dynamictexture;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShapedDynamicTextureRecipe extends ShapedRecipe {
    public static final String ID = "crafting_shaped_dynamic_texture";
    public static final IRecipeSerializer<ShapedDynamicTextureRecipe> SERIALIZER = new Serializer();

    public ShapedDynamicTextureRecipe(ShapedRecipe parent) {
        super(parent.getId(), parent.getGroup(), parent.getRecipeWidth(), parent.getRecipeHeight(), parent.getIngredients(), parent.getRecipeOutput());
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
        // Copied from vanilla, check if any pattern matches, but for all possible offsets
        return this.checkMaterial(inv) != null;
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
        ItemStack result = super.getRecipeOutput().copy();
        ItemStack material = this.checkMaterial(inv);
        if((result.getItem() instanceof BlockItemDynamicTexture) && (material != null)) {
            ((BlockItemDynamicTexture) result.getItem()).setMaterial(result, material);
        }
        return result;
    }

    @Nullable
    protected ItemStack checkMaterial(@Nonnull CraftingInventory inv) {
        // Mostly copied with vanilla, with an additional check if all dynamic texture ingredients are equivalent
        ItemStack material;
        for(int i = 0; i <= inv.getWidth() - this.getRecipeWidth(); ++i) {
            for(int j = 0; j <= inv.getHeight() - this.getRecipeHeight(); ++j) {
                material = this.checkMaterialWithOffset(inv, i, j, true);
                if (material != null) {
                    return material;
                }
                material = this.checkMaterialWithOffset(inv, i, j, false);
                if (material != null) {
                    return material;
                }
            }
        }
        return null;
    }

    @Nullable
    protected ItemStack checkMaterialWithOffset(CraftingInventory craftingInventory, int width, int height, boolean flag) {
        // Also mostly copied with vanilla, with an additional check if all dynamic texture ingredients are equivalent
        ItemStack material = null;
        for(int i = 0; i < craftingInventory.getWidth(); ++i) {
            for(int j = 0; j < craftingInventory.getHeight(); ++j) {
                int k = i - width;
                int l = j - height;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.getRecipeWidth() && l < this.getRecipeHeight()) {
                    if (flag) {
                        ingredient = this.getIngredients().get(this.getRecipeWidth() - k - 1 + l * this.getRecipeWidth());
                    } else {
                        ingredient = this.getIngredients().get(k + l * this.getRecipeWidth());
                    }
                }
                ItemStack stackInSlot = craftingInventory.getStackInSlot(i + j * craftingInventory.getWidth());
                if (!ingredient.test(stackInSlot)) {
                    return null;
                }
                // Dynamic texture check
                if(ingredient instanceof DynamicTextureIngredient) {
                    if(material == null) {
                        material = stackInSlot.copy();
                        material.setCount(1);
                    } else {
                        if(!ItemStack.areItemsEqual(material, stackInSlot)) {
                            return null;
                        }
                    }
                } else if(ingredient instanceof DynamicTextureParentIngredient) {
                    ItemStack materialStack = ((BlockItemDynamicTexture) stackInSlot.getItem()).getMaterial(stackInSlot);
                    if(material == null) {
                        material = materialStack.copy();
                        material.setCount(1);
                    } else {
                        if(!ItemStack.areItemsEqual(material, materialStack)) {
                            return null;
                        }
                    }
                }
            }
        }
        return material;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<ShapedDynamicTextureRecipe>, IInfRecipeSerializer {

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
        public ShapedDynamicTextureRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            return this.convert(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, json));
        }

        @Nullable
        @Override
        public ShapedDynamicTextureRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            return this.convert(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer));
        }

        protected ShapedDynamicTextureRecipe convert(@Nullable ShapedRecipe recipe) {
            return recipe == null ? null : new ShapedDynamicTextureRecipe(recipe);
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, @Nonnull ShapedDynamicTextureRecipe recipe) {
            IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe);
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
