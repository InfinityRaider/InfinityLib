package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.block.IInfinityBlock;
import com.infinityraider.infinitylib.block.tile.IInfinityTileEntityType;
import com.infinityraider.infinitylib.config.Config;
import com.infinityraider.infinitylib.container.IInfinityContainerMenuType;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.crafting.RecipeSerializers;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import com.infinityraider.infinitylib.potion.IInfinityPotionEffect;
import com.infinityraider.infinitylib.enchantment.EnchantmentBase;
import com.infinityraider.infinitylib.enchantment.IInfinityEnchantment;
import com.infinityraider.infinitylib.entity.EntityHandler;
import com.infinityraider.infinitylib.entity.IInfinityEntityType;
import com.infinityraider.infinitylib.entity.IMobEntityType;
import com.infinityraider.infinitylib.fluid.IInfinityFluid;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.loot.IInfLootModifierSerializer;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.particle.IInfinityParticleType;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.infinitylib.sound.IInfinitySoundEvent;
import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import com.infinityraider.infinitylib.utility.ReflectionHelper;
import com.infinityraider.infinitylib.world.IInfStructure;
import com.infinityraider.infinitylib.world.StructureRegistry;
import com.mojang.brigadier.StringReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
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
        this.registerEventHandler(EntityHandler.getInstance());
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
        RecipeSerializers.registerSerializers();
    }

    @Override
    default void onServerAboutToStartEvent(final ServerAboutToStartEvent event) {
        StructureRegistry.getInstance().injectStructures(event.getServer().registryAccess());
    }

    default void forceClientRenderUpdate(BlockPos pos) {}

    default void initItemRenderer(Consumer<IItemRenderProperties> consumer) {}

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
        this.registerStructures(mod);
    }

    default void registerBlocks(InfinityMod<?,?> mod) {
        // Register blocks
        this.registerObjects(mod, mod.getModBlockRegistry(), Classes.BLOCK, ForgeRegistries.BLOCKS, "Block");
    }

    default void registerTiles(InfinityMod<?,?> mod) {
        // Register tiles
        this.registerObjects(mod, mod.getModTileRegistry(), Classes.TILE_ENTITY_TYPE, ForgeRegistries.BLOCK_ENTITIES, "Tile Entity Type");
    }

    default void registerItems(InfinityMod<?,?> mod) {
        // Register items
        this.registerObjects(mod, mod.getModItemRegistry(), Classes.ITEM, ForgeRegistries.ITEMS, "Item");
    }

    default void registerFluids(InfinityMod<?,?> mod) {
        // Register fluids
        this.registerObjects(mod, mod.getModFluidRegistry(), Classes.FLUID, ForgeRegistries.FLUIDS, "Fluid");
    }

    default void registerEnchantments(InfinityMod<?,?> mod) {
        // Register enchantments
        this.registerObjects(mod, mod.getModEnchantmentRegistry(), Classes.ENCHANTMENT, ForgeRegistries.ENCHANTMENTS, "Enchantment", enchant -> {
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
        this.registerObjects(mod, mod.getModEntityRegistry(), Classes.ENTITY_TYPE, ForgeRegistries.ENTITIES, "Entity Type", entityType -> {
            // Tasks for living entities registration:
            if (entityType instanceof IMobEntityType) {
                IMobEntityType livingEntityType = (IMobEntityType) entityType;
                //  Attributes
                EntityHandler.getInstance().registerAttribute(livingEntityType.cast(), livingEntityType.createCustomAttributes());
                // Spawn egg
                livingEntityType.getSpawnEggData().ifPresent(data -> {
                    Item.Properties properties = new Item.Properties();
                    CreativeModeTab tab = data.tab();
                    if (tab != null) {
                        properties = properties.tab(tab);
                    }
                    // Create spawn egg
                    SpawnEggItem egg = new SpawnEggItem(livingEntityType.cast(), data.primaryColor(), data.secondaryColor(), properties);
                    // Register spawn egg
                    itemRegister.register(livingEntityType.getInternalName() + "_spawn_egg", () -> egg);
                });
                // Natural spawning
                livingEntityType.getSpawnRules().forEach(rule ->
                        EntityHandler.getInstance().registerSpawnRule(livingEntityType.cast(), rule)
                );
            }
        });
        itemRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    default void registerSounds(InfinityMod<?,?> mod) {
        // Register enchantments
        this.registerObjects(mod, mod.getModSoundRegistry(), Classes.SOUND_EVENT, ForgeRegistries.SOUND_EVENTS, "Sound Event");
    }

    default void registerParticles(InfinityMod<?,?> mod) {
        // Register particles
        this.registerObjects(mod, mod.getModParticleRegistry(), Classes.PARTICLE_TYPE, ForgeRegistries.PARTICLE_TYPES, "Particle Type",
                type -> this.onParticleRegistration((IInfinityParticleType<?>) type));
    }

    default <T extends ParticleOptions> void onParticleRegistration(IInfinityParticleType<T> particleType) {}

    default void registerEffects(InfinityMod<?,?> mod) {
        // Register effects
        this.registerObjects(mod, mod.getModEffectRegistry(), Classes.POTION_EFFECT, ForgeRegistries.MOB_EFFECTS, "Potion Effect Type");
    }

    default void registerContainers(InfinityMod<?,?> mod) {
        // Register containers
        this.registerObjects(mod, mod.getModContainerRegistry(), Classes.CONTAINER_MENU_TYPE, ForgeRegistries.CONTAINERS, "Container Menu Type", containerType -> {
            if(containerType instanceof IInfinityContainerMenuType) {
                this.registerGuiContainer((IInfinityContainerMenuType) containerType);
            }
        });
    }

    default void registerRecipeSerializers(InfinityMod<?,?> mod) {
        // Register recipe serializers
        this.registerObjects(mod, mod.getModRecipeSerializerRegistry(), Classes.RECIPE_SERIALIZER, ForgeRegistries.RECIPE_SERIALIZERS, "Recipe Type", recipe -> {
            if (recipe instanceof IInfRecipeSerializer) {
                // Also register the recipe's ingredient serializers
                ((IInfRecipeSerializer) recipe).getIngredientSerializers().forEach(RecipeSerializers::registerSerializer);
            }
        });
        // Register ingredient serializers
        if(mod.getModRecipeSerializerRegistry() != null) {
            ReflectionHelper.forEachValueIn(mod.getModRecipeSerializerRegistry(), IInfIngredientSerializer.class, RecipeSerializers::registerSerializer);
        }
    }

    default void registerLootModifiers(InfinityMod<?,?> mod) {
        this.registerObjects(mod, mod.getModLootModifierSerializerRegistry(), Classes.LOOT_MODIFIER, ForgeRegistries.LOOT_MODIFIER_SERIALIZERS.get(), "Loot Modifier");
    }

    default void registerStructures(InfinityMod<?,?> mod) {
        Object registry = mod.getStructureRegistry();
        if(registry != null) {
            ReflectionHelper.forEachValueIn(registry, IInfStructure.class, structure -> {
                if(structure != null) {
                    StructureRegistry.getInstance().registerStructure(structure);
                }
            });
        }
    }

    default void registerGuiContainer(IInfinityContainerMenuType containerType) {}

    default <T extends IForgeRegistryEntry<T>> void registerObjects(InfinityMod<?,?> mod, Class<?> modRegistry,
                                                                    Class<? extends IInfinityRegistrable<T>> clazz,
                                                                    IForgeRegistry<T> targetRegistry,
                                                                    String type) {
        this.registerObjects(mod, modRegistry, clazz, targetRegistry, type, (obj) -> {});
    }

    default <T extends IForgeRegistryEntry<T>> void registerObjects(InfinityMod<?,?> mod, Class<?> modRegistry,
                                                                    Class<? extends IInfinityRegistrable<T>> clazz,
                                                                    IForgeRegistry<T> registry, String type,
                                                                    Consumer<IInfinityRegistrable<T>> tasks) {
        // If the mod registry is missing, skip
        if (modRegistry != null) {
            // Register an event handler to register the objects
            FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(
                    registry.getRegistrySuperType(),
                    (RegistryEvent.Register<T> event) -> ReflectionHelper.forEachValueIn(
                            modRegistry,
                            clazz,
                            object -> this.registerObject(mod, object, event.getRegistry(), type, tasks))
            );
        }
    }

    default <T extends IForgeRegistryEntry<T>> void registerObject(InfinityMod<?,?> mod, IInfinityRegistrable<T> object,
                                                                   IForgeRegistry<T> register, String type,
                                                                   Consumer<IInfinityRegistrable<T>> tasks) {
        if(object.isEnabled()) {
            mod.getLogger().debug(" - Registering " + type + ": " + mod.getModId() + ":" + object.getInternalName());
            object.cast().setRegistryName(new ResourceLocation(mod.getModId(), object.getInternalName()));
            register.register(object.cast());
            tasks.accept(object);
        }
    }


    default void registerRenderers(InfinityMod<?,?> mod) {}

    @SuppressWarnings("unchecked")
    final class Classes {
        private static final Class<? extends IInfinityRegistrable<Block>> BLOCK = IInfinityBlock.class;

        private static final Class<? extends IInfinityRegistrable<BlockEntityType<?>>> TILE_ENTITY_TYPE = IInfinityTileEntityType.class;

        private static final Class<? extends IInfinityRegistrable<Item>> ITEM = IInfinityItem.class;

        private static final Class<? extends IInfinityRegistrable<Fluid>> FLUID = IInfinityFluid.class;

        private static final Class<? extends  IInfinityRegistrable<Enchantment>> ENCHANTMENT = IInfinityEnchantment.class;

        private static final Class<? extends IInfinityRegistrable<EntityType<?>>> ENTITY_TYPE = IInfinityEntityType.class;

        private static final Class<? extends IInfinityRegistrable<SoundEvent>> SOUND_EVENT = IInfinitySoundEvent.class;

        private static final Class<? extends IInfinityRegistrable<ParticleType<?>>> PARTICLE_TYPE;

        private static final Class<? extends IInfinityRegistrable<MobEffect>> POTION_EFFECT = IInfinityPotionEffect.class;

        private static final Class<? extends IInfinityRegistrable<MenuType<?>>> CONTAINER_MENU_TYPE = IInfinityContainerMenuType.class;

        private static final Class<? extends IInfinityRegistrable<RecipeSerializer<?>>> RECIPE_SERIALIZER = IInfRecipeSerializer.class;

        private static final Class<? extends IInfinityRegistrable<GlobalLootModifierSerializer<?>>> LOOT_MODIFIER = IInfLootModifierSerializer.class;

        // DAMN GENERICS
        // tldr: yes this is an ugly hack to get the generics to work, if you know a better way, PR pls
        static {
            PARTICLE_TYPE = (Class<? extends IInfinityRegistrable<ParticleType<?>>>) (new IInfinityParticleType<>() {
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
                public ParticleOptions deserializeData(@Nonnull StringReader reader)  {
                    return null;
                }

                @Override
                public ParticleOptions readData(@Nonnull FriendlyByteBuf buffer) {
                    return null;
                }

                @Nonnull
                @Override
                public ParticleFactorySupplier<ParticleOptions> particleFactorySupplier() {
                    return () -> null;
                }
            }).getClass().getInterfaces()[0];
        }

        private Classes() {}
    }
}
