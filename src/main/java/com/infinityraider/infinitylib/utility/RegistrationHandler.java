package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.block.IInfinityBlock;
import com.infinityraider.infinitylib.block.tile.IInfinityTileEntityType;
import com.infinityraider.infinitylib.container.IInfinityContainerMenuType;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import com.infinityraider.infinitylib.crafting.RecipeSerializers;
import com.infinityraider.infinitylib.enchantment.EnchantmentBase;
import com.infinityraider.infinitylib.enchantment.IInfinityEnchantment;
import com.infinityraider.infinitylib.entity.EntityHandler;
import com.infinityraider.infinitylib.entity.IInfinityEntityType;
import com.infinityraider.infinitylib.entity.IMobEntityType;
import com.infinityraider.infinitylib.fluid.IInfinityFluid;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.loot.IInfLootModifierSerializer;
import com.infinityraider.infinitylib.particle.IInfinityParticleType;
import com.infinityraider.infinitylib.potion.IInfinityPotionEffect;
import com.infinityraider.infinitylib.sound.IInfinitySoundEvent;
import com.mojang.brigadier.StringReader;
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
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class RegistrationHandler {
    private final InfinityMod<?,?> mod;

    public RegistrationHandler(InfinityMod<?,?> mod) {
        this.mod = mod;
    }

    public InfinityMod<?,?> getMod() {
        return this.mod;
    }

    protected  <T extends IForgeRegistryEntry<T>> void registerObjects(InfinityMod<?,?> mod, Class<?> modRegistry,
                                                                    Class<? extends IInfinityRegistrable<T>> clazz,
                                                                    IForgeRegistry<T> targetRegistry,
                                                                    String type) {
        this.registerObjects(mod, modRegistry, clazz, targetRegistry, type, (obj) -> {});
    }

    protected <T extends IForgeRegistryEntry<T>> void registerObjects(InfinityMod<?,?> mod, Class<?> modRegistry,
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

    protected <T extends IForgeRegistryEntry<T>> void registerObject(InfinityMod<?,?> mod, IInfinityRegistrable<T> object,
                                                                   IForgeRegistry<T> register, String type,
                                                                   Consumer<IInfinityRegistrable<T>> tasks) {
        if(object.isEnabled()) {
            mod.getLogger().debug(" - Registering " + type + ": " + mod.getModId() + ":" + object.getInternalName());
            object.cast().setRegistryName(new ResourceLocation(mod.getModId(), object.getInternalName()));
            register.register(object.cast());
            tasks.accept(object);
        }
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        // Register blocks
        this.registerObjects(this.getMod(), this.getMod().getModBlockRegistry(), Classes.BLOCK, event.getRegistry(), "Block");
    }

    @SubscribeEvent
    public void registerTiles(RegistryEvent.Register<BlockEntityType<?>> event) {
        // Register tiles
        this.registerObjects(this.getMod(), this.getMod().getModTileRegistry(), Classes.TILE_ENTITY_TYPE, event.getRegistry(), "Tile Entity Type");
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        // Register items
        this.registerObjects(this.getMod(), this.getMod().getModItemRegistry(), Classes.ITEM, event.getRegistry(), "Item");
    }

    @SubscribeEvent
    public void registerFluids(RegistryEvent.Register<Fluid> event) {
        // Register fluids
        this.registerObjects(this.getMod(), this.getMod().getModFluidRegistry(), Classes.FLUID, event.getRegistry(), "Fluid");
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        // Register enchantments
        this.registerObjects(this.getMod(), this.getMod().getModEnchantmentRegistry(), Classes.ENCHANTMENT, event.getRegistry(), "Enchantment", enchant -> {
            if(enchant instanceof EnchantmentBase) {
                ((EnchantmentBase) enchant).setDisplayName("enchantment." + mod.getModId() + "." + enchant.getInternalName());
            }
        });
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        // Create a deferred item register for the spawn eggs
        DeferredRegister<Item> itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, mod.getModId());
        // Register entities
        this.registerObjects(this.getMod(), this.getMod().getModEntityRegistry(), Classes.ENTITY_TYPE, event.getRegistry(), "Entity Type", entityType -> {
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

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        // Register enchantments
        this.registerObjects(this.getMod(), this.getMod().getModSoundRegistry(), Classes.SOUND_EVENT, event.getRegistry(), "Sound Event");
    }

    @SubscribeEvent
    public void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        // Register particles
        this.registerObjects(this.getMod(), this.getMod().getModParticleRegistry(), Classes.PARTICLE_TYPE, event.getRegistry(), "Particle Type",
                type -> InfinityLib.instance.proxy().onParticleRegistration((IInfinityParticleType<?>) type));
    }



    @SubscribeEvent
    public void registerEffects(RegistryEvent.Register<MobEffect> event) {
        // Register effects
        this.registerObjects(this.getMod(), this.getMod().getModEffectRegistry(), Classes.MOB_EFFECT, event.getRegistry(), "Mob Effect");
    }

    @SubscribeEvent
    public void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        // Register containers
        this.registerObjects(this.getMod(), this.getMod().getModContainerRegistry(), Classes.CONTAINER_MENU_TYPE, event.getRegistry(), "Container Menu Type", containerType -> {
            if(containerType instanceof IInfinityContainerMenuType) {
                InfinityLib.instance.proxy().registerGuiContainer((IInfinityContainerMenuType) containerType);
            }
        });
    }

    @SubscribeEvent
    public void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        // Register recipe serializers
        this.registerObjects(this.getMod(), this.getMod().getModRecipeSerializerRegistry(), Classes.RECIPE_SERIALIZER, event.getRegistry(), "Recipe Type", recipe -> {
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

    @SubscribeEvent
    public void registerLootModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        this.registerObjects(this.getMod(), this.getMod().getModLootModifierSerializerRegistry(), Classes.LOOT_MODIFIER, event.getRegistry(), "Loot Modifier");
    }

    @SuppressWarnings("unchecked")
    private static final class Classes {
        private static final Class<? extends IInfinityRegistrable<Block>> BLOCK = IInfinityBlock.class;

        private static final Class<? extends IInfinityRegistrable<BlockEntityType<?>>> TILE_ENTITY_TYPE = IInfinityTileEntityType.class;

        private static final Class<? extends IInfinityRegistrable<Item>> ITEM = IInfinityItem.class;

        private static final Class<? extends IInfinityRegistrable<Fluid>> FLUID = IInfinityFluid.class;

        private static final Class<? extends  IInfinityRegistrable<Enchantment>> ENCHANTMENT = IInfinityEnchantment.class;

        private static final Class<? extends IInfinityRegistrable<EntityType<?>>> ENTITY_TYPE = IInfinityEntityType.class;

        private static final Class<? extends IInfinityRegistrable<SoundEvent>> SOUND_EVENT = IInfinitySoundEvent.class;

        private static final Class<? extends IInfinityRegistrable<ParticleType<?>>> PARTICLE_TYPE;

        private static final Class<? extends IInfinityRegistrable<MobEffect>> MOB_EFFECT = IInfinityPotionEffect.class;

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
