package com.infinityraider.infinitylib.utility.registration;

import com.infinityraider.infinitylib.InfinityMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryInitializer<T extends IForgeRegistryEntry<?> & IInfinityRegistrable<?>> implements Supplier<T> {
    private final Type type;
    private final Supplier<T> constructor;

    private RegistryObject<T> access;

    private RegistryInitializer(Type type, Supplier<T> constructor) {
        this.type = type;
        this.constructor = constructor;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public final T get() {
        return this.access == null ? null : this.access.get();
    }

    @SuppressWarnings("unchecked")
    protected void register(InfinityMod<?, ?> mod, IForgeRegistry<? super T>  registry, Consumer<? super T> tasks) {
        T object = this.constructor.get();
        if(object.isEnabled()) {
            mod.getLogger().debug(" - Registering " + this.getType().descr() + ": " + mod.getModId() + ":" + object.getInternalName());
            ResourceLocation id = new ResourceLocation(mod.getModId(), object.getInternalName());
            object.setRegistryName(id);
            registry.register(object);
            tasks.accept(object);
            this.access = (RegistryObject<T>) RegistryObject.create(id, registry);
        }
    }

    protected static <T extends Block & IInfinityRegistrable<?>> RegistryInitializer<T> block(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.BLOCK, supplier);
    }

    protected static <T extends BlockEntityType<?> & IInfinityRegistrable<?>> RegistryInitializer<T> blockEntity(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.BLOCK_ENTITY, supplier);
    }

    protected static <T extends Item & IInfinityRegistrable<?>> RegistryInitializer<T> item(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.ITEM, supplier);
    }

    protected static <T extends Fluid & IInfinityRegistrable<?>> RegistryInitializer<T> fluid(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.FLUID, supplier);
    }

    protected static <T extends Enchantment & IInfinityRegistrable<?>> RegistryInitializer<T> enchantment(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.ENCHANTMENT, supplier);
    }

    protected static <T extends EntityType<?> & IInfinityRegistrable<?>> RegistryInitializer<T> entity(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.ENTITY, supplier);
    }

    protected static <T extends SoundEvent & IInfinityRegistrable<?>> RegistryInitializer<T> sound(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.SOUND_EVENT, supplier);
    }

    protected static <T extends ParticleType<?> & IInfinityRegistrable<?>> RegistryInitializer<T> particle(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.PARTICLE_TYPE, supplier);
    }

    protected static <T extends MobEffect & IInfinityRegistrable<?>> RegistryInitializer<T> mobEffect(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.MOB_EFFECT, supplier);
    }

    protected static <T extends MenuType<?> & IInfinityRegistrable<?>> RegistryInitializer<T> menuType(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.MENU_TYPE, supplier);
    }

    protected static <T extends RecipeSerializer<?> & IInfinityRegistrable<?>> RegistryInitializer<T> recipe(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.RECIPE, supplier);
    }

    protected static <T extends GlobalLootModifierSerializer<?> & IInfinityRegistrable<?>> RegistryInitializer<T> loot(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.LOOT, supplier);
    }

    public enum Type {
        BLOCK("Block", InfinityMod::getModBlockRegistry),
        BLOCK_ENTITY("Block Entity Type", InfinityMod::getModTileRegistry),
        ITEM("Item", InfinityMod::getModItemRegistry),
        FLUID("Fluid", InfinityMod::getModFluidRegistry),
        ENCHANTMENT("Enchantment", InfinityMod::getModEnchantmentRegistry),
        ENTITY("Entity", InfinityMod::getModEntityRegistry),
        SOUND_EVENT("Sound Event", InfinityMod::getModSoundRegistry),
        PARTICLE_TYPE("Particle Type", InfinityMod::getModParticleRegistry),
        MOB_EFFECT("Mob Effect", InfinityMod::getModPotionTypeRegistry),
        MENU_TYPE("Menu Type", InfinityMod::getModContainerRegistry),
        RECIPE("Recipe Serializer", InfinityMod::getModRecipeSerializerRegistry),
        LOOT("Global Loot Modifier Serializer", InfinityMod::getModLootModifierSerializerRegistry);

        private final String descr;
        private final Function<InfinityMod<?,?>, ModContentRegistry> registryGetter;

        Type(String type, Function<InfinityMod<?,?>, ModContentRegistry> registryGetter) {
            this.descr = type;
            this.registryGetter = registryGetter;
        }

        public String descr() {
            return this.descr;
        }

        @Nullable
        public ModContentRegistry getContent(InfinityMod<?,?> mod) {
            return this.registryGetter.apply(mod);
        }
    }

}

