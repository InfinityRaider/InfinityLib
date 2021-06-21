package com.infinityraider.infinitylib.compat.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.crafting.dynamictexture.IDynamicTextureIngredient;
import com.infinityraider.infinitylib.crafting.dynamictexture.ShapedDynamicTextureRecipe;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IExtendableRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICustomCraftingCategoryExtension;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.block.Block;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Size2i;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@mezz.jei.api.JeiPlugin
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JeiPlugin implements IModPlugin {

    public static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "compat_jei");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        // Extend Vanilla crafting recipe category with the dynamic texture crafting
        IExtendableRecipeCategory<ICraftingRecipe, ICraftingCategoryExtension> category = registration.getCraftingCategory();
        category.addCategoryExtension(ShapedDynamicTextureRecipe.class, Objects::nonNull, DynamicTextureRecipeExtension::new);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        // Register dynamic texture wood items
        BlockItemDynamicTexture.getAll().forEach(item -> registration.registerSubtypeInterpreter(item, (stack, context) -> {
            ResourceLocation id = item.getMaterial(stack).getItem().getRegistryName();
            return id == null ? "unknown" : id.toString();
        }));
    }

    private static final class DynamicTextureRecipeExtension implements ICustomCraftingCategoryExtension {
        private final ShapedDynamicTextureRecipe recipe;

        public DynamicTextureRecipeExtension(ShapedDynamicTextureRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void setIngredients(IIngredients ingredients) {
            // Fetch materials
            List<Block> materials = this.recipe.getSuitableMaterials();
            // Set inputs
            ingredients.setInputLists(
                    VanillaTypes.ITEM,
                    recipe.getIngredients().stream().map(ingredient -> {
                        if (ingredient instanceof IDynamicTextureIngredient) {
                            return materials.stream().map(((IDynamicTextureIngredient) ingredient)::asStackWithMaterial)
                                    .collect(Collectors.toList());
                        }
                        return Lists.newArrayList(ingredient.getMatchingStacks());
                    }).collect(Collectors.toList())
            );
            // Set outputs
            ingredients.setOutputLists(
                    VanillaTypes.ITEM,
                    ImmutableList.of(materials.stream().map(this.recipe::getResultWithMaterial).collect(Collectors.toList()))
            );
        }

        @Override
        public ResourceLocation getRegistryName() {
            return recipe.getId();
        }

        @Override
        public Size2i getSize() {
            return new Size2i(this.recipe.getRecipeWidth(), this.recipe.getRecipeHeight());
        }

        @Override
        public void setRecipe(IRecipeLayout layout, IIngredients ingredients) {
            // As the focused slot will not cycle, we need to set the focus to null
            layout.getIngredientsGroup(VanillaTypes.ITEM).setOverrideDisplayFocus(null);
            // Set ingredients
            layout.getItemStacks().set(ingredients);
        }
    }
}
