package com.infinityraider.infinitylib.crafting.dynamictexture;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShapedDynamicTextureRecipe extends ShapedRecipe {
    public static final String ID = "crafting_shaped_dynamic_texture";
    public static final RecipeSerializer<ShapedDynamicTextureRecipe> SERIALIZER = new Serializer();

    private List<Block> materials;

    public ShapedDynamicTextureRecipe(ShapedRecipe parent) {
        super(parent.getId(), parent.getGroup(), parent.getRecipeWidth(), parent.getRecipeHeight(), parent.getIngredients(), parent.getResultItem());
    }

    public List<Block> getSuitableMaterials() {
        if(this.materials == null) {
            this.materials = this.getIngredients().stream()
                    .filter(ingredient -> ingredient instanceof IDynamicTextureIngredient)
                    .findFirst()
                    .map(ingredient -> (IDynamicTextureIngredient) ingredient)
                    .map(IDynamicTextureIngredient::getTag)
                    .map(ITag::stream)
                    .orElse(Stream.empty())
                    .collect(Collectors.toList());
        }
        return this.materials;
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

    public ItemStack getResultWithoutMaterial() {
        return super.getResultItem().copy();
    }

    public ItemStack getResultWithMaterial(Block block) {
        return this.getResultWithMaterial(new ItemStack(block));
    }

    public ItemStack getResultWithMaterial(ItemStack material) {
        ItemStack stack = this.getResultWithoutMaterial();
        if(stack.getItem() instanceof BlockItemDynamicTexture && material != null) {
            ((BlockItemDynamicTexture) stack.getItem()).setMaterial(stack, material);
        }
        return stack;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level world) {
        // Copied from vanilla, check if any pattern matches, but for all possible offsets
        return this.checkMaterial(inv) != null;
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull CraftingContainer inv) {
        return this.getResultWithMaterial(this.checkMaterial(inv));
    }

    @Nullable
    protected ItemStack checkMaterial(@Nonnull CraftingContainer inv) {
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
    protected ItemStack checkMaterialWithOffset(CraftingContainer craftingInventory, int width, int height, boolean flag) {
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
                ItemStack stackInSlot = craftingInventory.getItem(i + j * craftingInventory.getWidth());
                if (!ingredient.test(stackInSlot)) {
                    return null;
                }
                // Dynamic texture check
                if(ingredient instanceof DynamicTextureIngredient) {
                    if(material == null) {
                        material = stackInSlot.copy();
                        material.setCount(1);
                    } else {
                        if(!ItemStack.matches(material, stackInSlot)) {
                            return null;
                        }
                    }
                } else if(ingredient instanceof DynamicTextureParentIngredient) {
                    ItemStack materialStack = ((BlockItemDynamicTexture) stackInSlot.getItem()).getMaterial(stackInSlot);
                    if(material == null) {
                        material = materialStack.copy();
                        material.setCount(1);
                    } else {
                        if(!ItemStack.matches(material, materialStack)) {
                            return null;
                        }
                    }
                }
            }
        }
        return material;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>>
            implements RecipeSerializer<ShapedDynamicTextureRecipe>, IInfRecipeSerializer {

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
        public ShapedDynamicTextureRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            return this.convert(RecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json));
        }

        @Nullable
        @Override
        public ShapedDynamicTextureRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
            return this.convert(RecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer));
        }

        protected ShapedDynamicTextureRecipe convert(@Nullable ShapedRecipe recipe) {
            return recipe == null ? null : new ShapedDynamicTextureRecipe(recipe);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull ShapedDynamicTextureRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
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
