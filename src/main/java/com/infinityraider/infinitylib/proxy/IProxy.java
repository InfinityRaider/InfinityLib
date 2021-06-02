package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.block.IInfinityBlock;
import com.infinityraider.infinitylib.block.tile.IInfinityTileEntityType;
import com.infinityraider.infinitylib.config.Config;
import com.infinityraider.infinitylib.container.IInfinityContainerType;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.crafting.RecipeSerializers;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import com.infinityraider.infinitylib.effect.IInfinityEffect;
import com.infinityraider.infinitylib.enchantment.EnchantmentBase;
import com.infinityraider.infinitylib.enchantment.IInfinityEnchantment;
import com.infinityraider.infinitylib.entity.AmbientSpawnHandler;
import com.infinityraider.infinitylib.entity.IInfinityEntityType;
import com.infinityraider.infinitylib.entity.IInfinityLivingEntityType;
import com.infinityraider.infinitylib.fluid.IInfinityFluid;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.loot.IInfLootModifierSerializer;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.particle.IInfinityParticleType;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.infinitylib.sound.IInfinitySoundEvent;
import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import com.infinityraider.infinitylib.utility.ReflectionHelper;
import com.mojang.brigadier.StringReader;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IProxy extends IProxyBase<Config> {

    @Override
    default  Function<ForgeConfigSpec.Builder, Config> getConfigConstructor() {
        return Config.Common::new;
    }

    @Override
    default void registerEventHandlers() {
        Module.getActiveModules().forEach(module -> module.getCommonEventHandlers().forEach(this::registerEventHandler));
        this.registerEventHandler(AmbientSpawnHandler.getInstance());
    }

    @Override
    default void registerCapabilities() {
        Module.getActiveModules().forEach(module -> module.getCapabilities().forEach(this::registerCapability));
    }

    @Override
    default void activateRequiredModules() {}

    @Override
    default void onCommonSetupEvent(FMLCommonSetupEvent event) {
        Module.getActiveModules().forEach(Module::init);
        RecipeSerializers.getInstance().registerSerializers();
    }

    default void forceClientRenderUpdate(BlockPos pos) {}

    /**
     * -------------------
     * REGISTERING METHODS
     * -------------------
     */

    default void registerRegistrables(InfinityMod<?,?> mod) {
        this.registerBlocks(mod);
        this.registerTiles(mod);
        this.registerItems(mod);
        this.registerFluids(mod);
        this.registerEnchantments(mod);
        this.registerEntities(mod);
        this.registerSounds(mod);
        this.registerParticles(mod);
        this.registerEffects(mod);
        this.registerContainers(mod);
        this.registerRecipeSerializers(mod);
        this.registerLootModifiers(mod);
    }

    default void registerBlocks(InfinityMod<?,?> mod) {
        // Register blocks
        this.registerObjects(mod, mod.getModBlockRegistry(), Classes.BLOCK, ForgeRegistries.BLOCKS);
    }

    default void registerTiles(InfinityMod<?,?> mod) {
        // Register tiles
        this.registerObjects(mod, mod.getModTileRegistry(), Classes.TILE_ENTITY_TYPE, ForgeRegistries.TILE_ENTITIES);
    }

    default void registerItems(InfinityMod<?,?> mod) {
        // Register items
        this.registerObjects(mod, mod.getModItemRegistry(), Classes.ITEM, ForgeRegistries.ITEMS);
    }

    default void registerFluids(InfinityMod<?,?> mod) {
        // Register fluids
        this.registerObjects(mod, mod.getModFluidRegistry(), Classes.FLUID, ForgeRegistries.FLUIDS);
    }

    default void registerEnchantments(InfinityMod<?,?> mod) {
        // Register enchantments
        this.registerObjects(mod, mod.getModEnchantmentRegistry(), Classes.ENCHANTMENT, ForgeRegistries.ENCHANTMENTS, enchant -> {
            if(enchant instanceof EnchantmentBase) {
                ((EnchantmentBase) enchant).setDisplayName("enchantment." + mod.getModId() + "." + enchant.getInternalName());
            }
        });
    }

    @SuppressWarnings("deprecation")
    default void registerEntities(InfinityMod<?,?> mod) {
        // Create a deferred item register for the spawn eggs
        DeferredRegister<Item> itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, mod.getModId());
        // Register entities
        this.registerObjects(mod, mod.getModEntityRegistry(), Classes.ENTITY_TYPE, ForgeRegistries.ENTITIES, entityType -> {
            // Tasks for living entities registration:
            if (entityType instanceof IInfinityLivingEntityType) {
                IInfinityLivingEntityType livingEntityType = (IInfinityLivingEntityType) entityType;
                //  Attributes
                GlobalEntityTypeAttributes.put(livingEntityType.cast(), livingEntityType.createCustomAttributes());
                // Spawn egg
                livingEntityType.getSpawnEggData().ifPresent(data -> {
                    Item.Properties properties = new Item.Properties();
                    ItemGroup tab = data.tab();
                    if (tab != null) {
                        properties = properties.group(tab);
                    }
                    // Create spawn egg
                    SpawnEggItem egg = new SpawnEggItem(livingEntityType.cast(), data.primaryColor(), data.secondaryColor(), properties);
                    // Register spawn egg
                    itemRegister.register(livingEntityType.getInternalName() + "_spawn_egg", () -> egg);
                });
                // Natural spawning
                livingEntityType.getSpawnRules().forEach(rule ->
                        AmbientSpawnHandler.getInstance().registerSpawnRule(livingEntityType.cast(), rule)
                );
            }
        });
        itemRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    default void registerSounds(InfinityMod<?,?> mod) {
        // Register enchantments
        this.registerObjects(mod, mod.getModSoundRegistry(), Classes.SOUND_EVENT, ForgeRegistries.SOUND_EVENTS);
    }

    default void registerParticles(InfinityMod<?,?> mod) {
        // Register particles
        this.registerObjects(mod, mod.getModParticleRegistry(), Classes.PARTICLE_TYPE, ForgeRegistries.PARTICLE_TYPES,
                type -> this.onParticleRegistration((IInfinityParticleType<?>) type));
    }

    default <T extends IParticleData> void onParticleRegistration(IInfinityParticleType<T> particleType) {}

    default void registerEffects(InfinityMod<?,?> mod) {
        // Register effects
        this.registerObjects(mod, mod.getModEffectRegistry(), Classes.EFFECT, ForgeRegistries.POTIONS);
    }

    default void registerContainers(InfinityMod<?,?> mod) {
        // Register containers
        this.registerObjects(mod, mod.getModContainerRegistry(), Classes.CONTAINER_TYPE, ForgeRegistries.CONTAINERS, containerType -> {
            if(containerType instanceof IInfinityContainerType) {
                this.registerGuiContainer((IInfinityContainerType) containerType);
            }
        });
    }

    default void registerRecipeSerializers(InfinityMod<?,?> mod) {
        // Register recipe serializers
        this.registerObjects(mod, mod.getModRecipeSerializerRegistry(), Classes.RECIPE_SERIALIZER, ForgeRegistries.RECIPE_SERIALIZERS, recipe -> {
            if (recipe instanceof IInfRecipeSerializer) {
                // Also register the recipe's ingredient serializers
                ((IInfRecipeSerializer) recipe).getIngredientSerializers().forEach(serializer ->
                        RecipeSerializers.getInstance().registerSerializer(serializer));
            }
        });
        // Register ingredient serializers
        if(mod.getModRecipeSerializerRegistry() != null) {
            ReflectionHelper.forEachValueIn(mod.getModRecipeSerializerRegistry(), IInfIngredientSerializer.class, serializer ->
                    RecipeSerializers.getInstance().registerSerializer(serializer)
            );
        }
    }

    default void registerLootModifiers(InfinityMod<?,?> mod) {
        this.registerObjects(mod, mod.getModLootModifierSerializerRegistry(), Classes.LOOT_MODIFIER, ForgeRegistries.LOOT_MODIFIER_SERIALIZERS);
    }

    default void registerGuiContainer(IInfinityContainerType containerType) {}

    default <T extends IForgeRegistryEntry<T>> void registerObjects(InfinityMod<?,?> mod, Object modRegistry,
                                                                    Class<? extends IInfinityRegistrable<T>> clazz,
                                                                    IForgeRegistry<T> targetRegistry) {
        this.registerObjects(mod, modRegistry, clazz, targetRegistry, (obj) -> {});
    }

    default <T extends IForgeRegistryEntry<T>> void registerObjects(InfinityMod<?,?> mod, Object modRegistry,
                                                                    Class<? extends IInfinityRegistrable<T>> clazz,
                                                                    IForgeRegistry<T> registry,
                                                                    Consumer<IInfinityRegistrable<T>> tasks) {
        // If the mod registry is missing, skip
        if (modRegistry != null) {
            // Register the objects
            DeferredRegister<T> register = DeferredRegister.create(registry, mod.getModId());
            ReflectionHelper.forEachValueIn(modRegistry, clazz, object ->
                    this.registerObject(mod, object, register, tasks));
            register.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    default <T extends IForgeRegistryEntry<T>> void registerObject(InfinityMod<?,?> mod, IInfinityRegistrable<T> object,
                                                                   DeferredRegister<T> register,
                                                                   Consumer<IInfinityRegistrable<T>> tasks) {
        if(object.isEnabled()) {
            mod.getLogger().debug(" - Registering: " + object.getInternalName());
            register.register(object.getInternalName(), object::cast);
            tasks.accept(object);
        }
    }


    default void registerRenderers(InfinityMod<?,?> mod) {}

    @SuppressWarnings("unchecked")
    final class Classes {
        private static final Class<? extends IInfinityRegistrable<Block>> BLOCK = IInfinityBlock.class;

        private static final Class<? extends IInfinityRegistrable<TileEntityType<?>>> TILE_ENTITY_TYPE = IInfinityTileEntityType.class;

        private static final Class<? extends IInfinityRegistrable<Item>> ITEM = IInfinityItem.class;

        private static final Class<? extends IInfinityRegistrable<Fluid>> FLUID = IInfinityFluid.class;

        private static final Class<? extends  IInfinityRegistrable<Enchantment>> ENCHANTMENT = IInfinityEnchantment.class;

        private static final Class<? extends IInfinityRegistrable<EntityType<?>>> ENTITY_TYPE = IInfinityEntityType.class;

        private static final Class<? extends IInfinityRegistrable<SoundEvent>> SOUND_EVENT = IInfinitySoundEvent.class;

        private static final Class<? extends IInfinityRegistrable<ParticleType<?>>> PARTICLE_TYPE;

        private static final Class<? extends IInfinityRegistrable<Effect>> EFFECT = IInfinityEffect.class;

        private static final Class<? extends IInfinityRegistrable<ContainerType<?>>> CONTAINER_TYPE = IInfinityContainerType.class;

        private static final Class<? extends IInfinityRegistrable<IRecipeSerializer<?>>> RECIPE_SERIALIZER = IInfRecipeSerializer.class;

        private static final Class<? extends IInfinityRegistrable<GlobalLootModifierSerializer<?>>> LOOT_MODIFIER = IInfLootModifierSerializer.class;

        // DAMN GENERICS
        // tldr: yes this is an ugly hack to get the generics to work, if you know a better way, PR pls
        static {
            PARTICLE_TYPE = (Class<? extends IInfinityRegistrable<ParticleType<?>>>) (new IInfinityParticleType<IParticleData>() {
                @Override
                public boolean isEnabled() {
                    return false;
                }

                @Nonnull
                @Override
                public String getInternalName() {
                    return "";
                }

                @Override
                public IParticleData deserializeData(@Nonnull StringReader reader)  {
                    return null;
                }

                @Override
                public IParticleData readData(@Nonnull PacketBuffer buffer) {
                    return null;
                }

                @Nonnull
                @Override
                public ParticleFactorySupplier<IParticleData> particleFactorySupplier() {
                    return () -> null;
                }
            }).getClass().getInterfaces()[0];
        }

        private Classes() {}
    }
}
