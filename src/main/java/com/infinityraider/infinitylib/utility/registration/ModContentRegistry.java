package com.infinityraider.infinitylib.utility.registration;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
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

    protected final <T extends Block & IInfinityRegistrable<?>> RegistryInitializer<T> block(Supplier<T> supplier) {
        return this.store(RegistryInitializer.block(supplier));
    }

    protected final <T extends BlockEntityType<?> & IInfinityRegistrable<?>> RegistryInitializer<T> blockEntity(Supplier<T> supplier) {
        return this.store(RegistryInitializer.blockEntity(supplier));
    }

    protected final <T extends Item & IInfinityRegistrable<?>> RegistryInitializer<T> item(Supplier<T> supplier) {
        return this.store(RegistryInitializer.item(supplier));
    }

    protected final <T extends Fluid & IInfinityRegistrable<?>> RegistryInitializer<T> fluid(Supplier<T> supplier) {
        return this.store(RegistryInitializer.fluid(supplier));
    }

    protected final <T extends Enchantment & IInfinityRegistrable<?>> RegistryInitializer<T> enchantment(Supplier<T> supplier) {
        return this.store(RegistryInitializer.enchantment(supplier));
    }

    protected final <T extends EntityType<?> & IInfinityRegistrable<?>> RegistryInitializer<T> entity(Supplier<T> supplier) {
        return this.store(RegistryInitializer.entity(supplier));
    }

    protected final <T extends SoundEvent & IInfinityRegistrable<?>> RegistryInitializer<T> sound(Supplier<T> supplier) {
        return this.store(RegistryInitializer.sound(supplier));
    }

    protected final <T extends ParticleType<?> & IInfinityRegistrable<?>> RegistryInitializer<T> particle(Supplier<T> supplier) {
        return this.store(RegistryInitializer.particle(supplier));
    }

    protected final <T extends MobEffect & IInfinityRegistrable<?>> RegistryInitializer<T> mobEffect(Supplier<T> supplier) {
        return this.store(RegistryInitializer.mobEffect(supplier));
    }

    protected final <T extends MenuType<?> & IInfinityRegistrable<?>> RegistryInitializer<T> menuType(Supplier<T> supplier) {
        return this.store(RegistryInitializer.menuType(supplier));
    }

    protected final <T extends RecipeSerializer<?> & IInfinityRegistrable<?>> RegistryInitializer<T> recipe(Supplier<T> supplier) {
        return this.store(RegistryInitializer.recipe(supplier));
    }

    protected final <T extends GlobalLootModifierSerializer<?> & IInfinityRegistrable<?>> RegistryInitializer<T> loot(Supplier<T> supplier) {
        return this.store(RegistryInitializer.loot(supplier));
    }

    private <T extends IForgeRegistryEntry<?> & IInfinityRegistrable<?>> RegistryInitializer<T> store(RegistryInitializer<T> initializer) {
        this.initializers.computeIfAbsent(initializer.getType(), (i) -> Sets.newIdentityHashSet()).add(initializer);
        return initializer;
    }

    Stream<RegistryInitializer<?>> stream(RegistryInitializer.Type type) {
        return this.initializers.containsKey(type) ? this.initializers.get(type).stream() : Stream.empty();
    }
}
