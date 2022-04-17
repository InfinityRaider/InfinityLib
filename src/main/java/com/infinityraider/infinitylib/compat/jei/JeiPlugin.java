package com.infinityraider.infinitylib.compat.jei;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.crafting.dynamictexture.IDynamicTextureIngredient;
import com.infinityraider.infinitylib.crafting.dynamictexture.ShapedDynamicTextureRecipe;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.IExtendableRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@mezz.jei.api.JeiPlugin
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JeiPlugin implements IModPlugin {

    public static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "compat_jei");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        // Extend Vanilla crafting recipe category with the dynamic texture crafting
        IExtendableRecipeCategory<CraftingRecipe, ICraftingCategoryExtension> category = registration.getCraftingCategory();
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

    private static final class DynamicTextureRecipeExtension implements ICraftingCategoryExtension {
        private final ShapedDynamicTextureRecipe recipe;

        public DynamicTextureRecipeExtension(ShapedDynamicTextureRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
            // Fetch materials
            List<ItemStack> inputs = focuses.getFocuses(VanillaTypes.ITEM, RecipeIngredientRole.INPUT)
                    .map(IFocus::getTypedValue)
                    .map(ITypedIngredient::getIngredient)
                    .map(stack -> {
                        if(stack.getItem() instanceof BlockItemDynamicTexture) {
                            return ((BlockItemDynamicTexture) stack.getItem()).getMaterial(stack);
                        } else {
                            return stack;
                        }
                    })
                    // make sure the stack is not empty
                    .filter(stack -> !stack.isEmpty())
                    // make sure the stack contains a valid item
                    .filter(stack -> this.recipe.getSuitableMaterials().stream().anyMatch(block -> stack.getItem() == block.asItem()))
                    .collect(Collectors.toList());
            List<ItemStack> outputs = focuses.getFocuses(VanillaTypes.ITEM, RecipeIngredientRole.OUTPUT)
                    .map(IFocus::getTypedValue)
                    .map(ITypedIngredient::getIngredient)
                    .filter(stack -> stack.getItem() instanceof BlockItemDynamicTexture)
                    .map(stack -> ((BlockItemDynamicTexture) stack.getItem()).getMaterial(stack))
                    .collect(Collectors.toList());
            // Set recipe based on inputs
            if(inputs.size() > 0) {
                this.setRecipeForMaterials(builder, craftingGridHelper, inputs);
            } else if (outputs.size() > 0) {
                this.setRecipeForMaterials(builder, craftingGridHelper, outputs);
            } else {
                List<ItemStack> materials = this.recipe.getSuitableMaterials().stream()
                        .map(ItemStack::new)
                        .collect(Collectors.toList());
                this.setRecipeForMaterials(builder, craftingGridHelper, materials);
            }
        }

        protected void setRecipeForMaterials(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, List<ItemStack> materials) {
            // Set inputs
            List<ItemStack> empty = Collections.emptyList();
            List<List<ItemStack>> ingredients = this.recipe.getIngredients().stream().map(ingredient -> {
                if (ingredient.isEmpty()) {
                    // if the ingredient is empty, nothing should be put in the JEI recipe
                    return empty;
                } else if (ingredient instanceof IDynamicTextureIngredient) {
                    // if the ingredient is a dynamic texture ingredient, set it to the correct material
                    return materials.stream().map(((IDynamicTextureIngredient) ingredient)::asStackWithMaterial)
                            .collect(Collectors.toList());
                } else {
                    // in any other case, the ingredient is the material itself
                    return materials;
                }
            }).collect(Collectors.toList());
            craftingGridHelper.setInputs(
                    builder,
                    VanillaTypes.ITEM,
                    ingredients,
                    this.recipe.getRecipeWidth(),
                    this.recipe.getRecipeHeight()
            );
            // Set outputs
            craftingGridHelper.setOutputs(
                    builder,
                    VanillaTypes.ITEM,
                    materials.stream().map(this.recipe::getResultWithMaterial).collect(Collectors.toList())
            );
        }

        @Override
        public ResourceLocation getRegistryName() {
            return recipe.getId();
        }

        @Override
        public int getWidth() {
            return this.recipe.getRecipeWidth();
        }

        @Override
        public int getHeight() {
            return this.recipe.getRecipeHeight();
        }
    }
}
