package com.infinityraider.infinitylib.utility.registration;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.block.IInfinityBlock;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.container.IInfinityContainerMenuType;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import com.infinityraider.infinitylib.enchantment.IInfinityEnchantment;
import com.infinityraider.infinitylib.entity.IInfinityEntityType;
import com.infinityraider.infinitylib.fluid.IInfinityFluid;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.loot.IInfLootModifierSerializer;
import com.infinityraider.infinitylib.particle.IInfinityParticleType;
import com.infinityraider.infinitylib.potion.IInfinityPotionEffect;
import com.infinityraider.infinitylib.sound.IInfinitySoundEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.EnumMap;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModContentRegistry {
    private final EnumMap<RegistryInitializer.Type, Set<RegistryInitializer<?>>> initializers;

    protected ModContentRegistry() {
        this.initializers = Maps.newEnumMap(RegistryInitializer.Type.class);
    }

    protected final <T extends IInfinityBlock> RegistryInitializer<T> block(Supplier<T> supplier) {
        return this.store(RegistryInitializer.block(supplier));
    }

    protected final <T extends InfinityTileEntityType<?>> RegistryInitializer<T> blockEntity(Supplier<T> supplier) {
        return this.store(RegistryInitializer.blockEntity(supplier));
    }

    protected final <T extends IInfinityItem> RegistryInitializer<T> item(Supplier<T> supplier) {
        return this.store(RegistryInitializer.item(supplier));
    }

    protected final <T extends IInfinityFluid> RegistryInitializer<T> fluid(Supplier<T> supplier) {
        return this.store(RegistryInitializer.fluid(supplier));
    }

    protected final <T extends IInfinityEnchantment> RegistryInitializer<T> enchantment(Supplier<T> supplier) {
        return this.store(RegistryInitializer.enchantment(supplier));
    }

    protected final <T extends IInfinityEntityType> RegistryInitializer<T> entity(Supplier<T> supplier) {
        return this.store(RegistryInitializer.entity(supplier));
    }

    protected final <T extends IInfinitySoundEvent> RegistryInitializer<T> sound(Supplier<T> supplier) {
        return this.store(RegistryInitializer.sound(supplier));
    }

    protected final <T extends IInfinityParticleType<?>> RegistryInitializer<T> particle(Supplier<T> supplier) {
        return this.store(RegistryInitializer.particle(supplier));
    }

    protected final <T extends IInfinityPotionEffect> RegistryInitializer<T> mobEffect(Supplier<T> supplier) {
        return this.store(RegistryInitializer.mobEffect(supplier));
    }

    protected final <T extends IInfinityContainerMenuType> RegistryInitializer<T> menuType(Supplier<T> supplier) {
        return this.store(RegistryInitializer.menuType(supplier));
    }

    protected final <T extends IInfRecipeSerializer<?>> RegistryInitializer<T> recipe(Supplier<T> supplier) {
        return this.store(RegistryInitializer.recipe(supplier));
    }

    protected final <T extends IInfLootModifierSerializer> RegistryInitializer<T> loot(Supplier<T> supplier) {
        return this.store(RegistryInitializer.loot(supplier));
    }

    private <T extends IInfinityRegistrable<? extends IForgeRegistryEntry<?>>> RegistryInitializer<T> store(RegistryInitializer<T> initializer) {
        this.initializers.computeIfAbsent(initializer.getType(), (i) -> Sets.newIdentityHashSet()).add(initializer);
        return initializer;
    }

    public Stream<RegistryInitializer<?>> stream(RegistryInitializer.Type type) {
        return this.initializers.containsKey(type) ? this.initializers.get(type).stream() : Stream.empty();
    }
}
