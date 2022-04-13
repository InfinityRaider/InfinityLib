package com.infinityraider.infinitylib.utility.registration;

import com.infinityraider.infinitylib.InfinityMod;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryInitializer<T extends IInfinityRegistrable<?>> implements Supplier<T> {
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

    @Nonnull
    @Override
    public final T get() {
        Objects.requireNonNull(this.access, () -> "Queried registry initializer entry too early");
        return this.access.get();
    }

    @SuppressWarnings("unchecked")
    protected void register(InfinityMod<?, ?> mod, IForgeRegistry<? super T>  registry, Consumer<? super T> tasks) {
        T object = this.constructor.get();
        if(object.isEnabled()) {
            mod.getLogger().debug(" - Registering " + this.getType().descr() + ": " + mod.getModId() + ":" + object.getInternalName());
            ResourceLocation id = new ResourceLocation(mod.getModId(), object.getInternalName());
            object.cast().setRegistryName(id);
            registry.register(object);
            tasks.accept(object);
            this.access = (RegistryObject<T>) RegistryObject.create(id, registry);
        }
    }

    protected static <T extends IInfinityBlock> RegistryInitializer<T> block(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.BLOCK, supplier);
    }

    protected static <T extends InfinityTileEntityType<?>> RegistryInitializer<T> blockEntity(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.BLOCK_ENTITY, supplier);
    }

    protected static <T extends IInfinityItem> RegistryInitializer<T> item(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.ITEM, supplier);
    }

    protected static <T extends IInfinityFluid> RegistryInitializer<T> fluid(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.FLUID, supplier);
    }

    protected static <T extends IInfinityEnchantment> RegistryInitializer<T> enchantment(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.ENCHANTMENT, supplier);
    }

    protected static <T extends IInfinityEntityType> RegistryInitializer<T> entity(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.ENTITY, supplier);
    }

    protected static <T extends IInfinitySoundEvent> RegistryInitializer<T> sound(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.SOUND_EVENT, supplier);
    }

    protected static <T extends IInfinityParticleType<?>> RegistryInitializer<T> particle(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.PARTICLE_TYPE, supplier);
    }

    protected static <T extends IInfinityPotionEffect> RegistryInitializer<T> mobEffect(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.MOB_EFFECT, supplier);
    }

    protected static <T extends IInfinityContainerMenuType> RegistryInitializer<T> menuType(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.MENU_TYPE, supplier);
    }

    protected static <T extends IInfRecipeSerializer<?>> RegistryInitializer<T> recipe(Supplier<T> supplier) {
        return new RegistryInitializer<>(Type.RECIPE, supplier);
    }

    protected static <T extends IInfLootModifierSerializer> RegistryInitializer<T> loot(Supplier<T> supplier) {
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

