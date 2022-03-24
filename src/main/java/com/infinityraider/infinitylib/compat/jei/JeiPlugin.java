package com.infinityraider.infinitylib.compat.jei;

import com.google.common.collect.Lists;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.crafting.dynamictexture.IDynamicTextureIngredient;
import com.infinityraider.infinitylib.crafting.dynamictexture.ShapedDynamicTextureRecipe;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.IExtendableRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
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
            List<Block> materials = this.recipe.getSuitableMaterials();
            // Set inputs
            craftingGridHelper.setInputs(
                    builder,
                    VanillaTypes.ITEM,
                    recipe.getIngredients().stream().map(ingredient -> {
                        if (ingredient instanceof IDynamicTextureIngredient) {
                            return materials.stream().map(((IDynamicTextureIngredient) ingredient)::asStackWithMaterial)
                                    .collect(Collectors.toList());
                        }
                        return Lists.newArrayList(ingredient.getItems());
                    }).collect(Collectors.toList()),
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
