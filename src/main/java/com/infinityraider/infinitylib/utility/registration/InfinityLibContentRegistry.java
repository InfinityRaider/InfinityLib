package com.infinityraider.infinitylib.utility.registration;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.block.IInfinityBlock;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.container.IInfinityContainerMenuType;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import com.infinityraider.infinitylib.crafting.dynamictexture.ShapedDynamicTextureRecipe;
import com.infinityraider.infinitylib.crafting.dynamictexture.ShapelessDynamicTextureRecipe;
import com.infinityraider.infinitylib.crafting.fallback.FallbackIngredient;
import com.infinityraider.infinitylib.enchantment.IInfinityEnchantment;
import com.infinityraider.infinitylib.entity.IInfinityEntityType;
import com.infinityraider.infinitylib.fluid.IInfinityFluid;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.loot.IInfLootModifierSerializer;
import com.infinityraider.infinitylib.particle.IInfinityParticleType;
import com.infinityraider.infinitylib.potion.IInfinityPotionEffect;
import com.infinityraider.infinitylib.sound.IInfinitySoundEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Supplier;

public class InfinityLibContentRegistry extends ModContentRegistry {
    private static final InfinityLibContentRegistry INSTANCE = new InfinityLibContentRegistry();
    
    public static InfinityLibContentRegistry getInstance() {
        return INSTANCE;
    }

    // ingredients
    public final IInfIngredientSerializer<FallbackIngredient> fallbackIngredient = FallbackIngredient.SERIALIZER;

    // recipes
    public final RegistryInitializer<IInfRecipeSerializer<ShapedDynamicTextureRecipe>> shapedDynamicTextureRecipe;
    public final RegistryInitializer<IInfRecipeSerializer<ShapelessDynamicTextureRecipe>> shapelessDynamicTextureRecipe;

    private InfinityLibContentRegistry() {
        super();
        this.shapedDynamicTextureRecipe = this.recipe(() -> ShapedDynamicTextureRecipe.SERIALIZER);
        this.shapelessDynamicTextureRecipe = this.recipe(() -> ShapelessDynamicTextureRecipe.SERIALIZER);
    }

    public <T extends IInfinityBlock> RegistryInitializer<T> registerBlock(Supplier<T> supplier) {
        return super.block(supplier);
    }

    public <T extends InfinityTileEntityType<?>> RegistryInitializer<T> registerBlockEntity(Supplier<T> supplier) {
        return super.blockEntity(supplier);
    }

    public <T extends IInfinityItem> RegistryInitializer<T> registerItem(Supplier<T> supplier) {
        return super.item(supplier);
    }

    public <T extends IInfinityFluid> RegistryInitializer<T> registerFluid(Supplier<T> supplier) {
        return super.fluid(supplier);
    }

    public <T extends IInfinityEnchantment> RegistryInitializer<T> registerEnchantment(Supplier<T> supplier) {
        return super.enchantment(supplier);
    }

    public <T extends IInfinityEntityType> RegistryInitializer<T> registerEntity(Supplier<T> supplier) {
        return super.entity(supplier);
    }

    public <T extends IInfinitySoundEvent> RegistryInitializer<T> registerSound(Supplier<T> supplier) {
        return super.sound(supplier);
    }

    public <T extends IInfinityParticleType<?>> RegistryInitializer<T> registerParticle(Supplier<T> supplier) {
        return super.particle(supplier);
    }

    public <T extends IInfinityPotionEffect> RegistryInitializer<T> registerMobEffect(Supplier<T> supplier) {
        return super.mobEffect(supplier);
    }

    public <T extends IInfinityContainerMenuType> RegistryInitializer<T> registerMenuType(Supplier<T> supplier) {
        return super.menuType(supplier);
    }

    public <T extends IInfRecipeSerializer<?>> RegistryInitializer<T> registerRecipe(Supplier<T> supplier) {
        return super.recipe(supplier);
    }

    public <T extends IInfLootModifierSerializer> RegistryInitializer<T> registerLoot(Supplier<T> supplier) {
        return super.loot(supplier);
    }
}
