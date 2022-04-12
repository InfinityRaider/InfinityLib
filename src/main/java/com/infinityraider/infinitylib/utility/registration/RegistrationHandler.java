package com.infinityraider.infinitylib.utility.registration;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.container.IInfinityContainerMenuType;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import com.infinityraider.infinitylib.crafting.RecipeSerializers;
import com.infinityraider.infinitylib.enchantment.EnchantmentBase;
import com.infinityraider.infinitylib.entity.EntityHandler;
import com.infinityraider.infinitylib.entity.IMobEntityType;
import com.infinityraider.infinitylib.particle.IInfinityParticleType;
import com.infinityraider.infinitylib.utility.ReflectionHelper;
import net.minecraft.core.particles.ParticleType;
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

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class RegistrationHandler {
    private final InfinityMod<?, ?> mod;

    public RegistrationHandler(InfinityMod<?, ?> mod) {
        this.mod = mod;
    }

    public InfinityMod<?, ?> getMod() {
        return this.mod;
    }

    private <T extends IForgeRegistryEntry<T>> void registerObjects(RegistryInitializer.Type type, IForgeRegistry<T> registry) {
        this.registerObjects(type, registry, (object) -> {
        });
    }

    private <T extends IForgeRegistryEntry<T>> void registerObjects(RegistryInitializer.Type type, IForgeRegistry<T> registry, Consumer<T> tasks) {
        ModContentRegistry content = type.getContent(this.getMod());
        if (content == null) {
            return;
        }
        content.stream(type).forEach(object -> this.registerObject(object, registry, tasks));
    }

    @SuppressWarnings("unchecked")
    private  <T extends IForgeRegistryEntry<T> & IInfinityRegistrable<T>> void registerObject(
            RegistryInitializer<?> object, IForgeRegistry<?> registry, Consumer<?> tasks) {
        ((RegistryInitializer<T>) object).register(this.getMod(), (IForgeRegistry<? super T>) registry, (Consumer<? super T>) tasks);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        // Register blocks
        this.registerObjects(RegistryInitializer.Type.BLOCK, event.getRegistry());
    }

    @SubscribeEvent
    public void registerTiles(RegistryEvent.Register<BlockEntityType<?>> event) {
        // Register tiles
        this.registerObjects(RegistryInitializer.Type.BLOCK_ENTITY, event.getRegistry());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        // Register items
        this.registerObjects(RegistryInitializer.Type.ITEM, event.getRegistry());
    }

    @SubscribeEvent
    public void registerFluids(RegistryEvent.Register<Fluid> event) {
        // Register fluids
        this.registerObjects(RegistryInitializer.Type.FLUID, event.getRegistry());
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        // Register enchantments
        this.registerObjects(RegistryInitializer.Type.ENCHANTMENT, event.getRegistry(), enchant -> {
            if (enchant instanceof EnchantmentBase) {
                ((EnchantmentBase) enchant).setDisplayName("enchantment." + mod.getModId() + "." + ((EnchantmentBase) enchant).getInternalName());
            }
        });
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        // Create a deferred item register for the spawn eggs
        DeferredRegister<Item> itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, mod.getModId());
        // Register entities
        this.registerObjects(RegistryInitializer.Type.ENTITY, event.getRegistry(), entityType -> {
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
        // Register sound events
        this.registerObjects(RegistryInitializer.Type.SOUND_EVENT, event.getRegistry());
    }

    @SubscribeEvent
    public void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        // Register particles
        this.registerObjects(RegistryInitializer.Type.PARTICLE_TYPE, event.getRegistry(), type ->
                InfinityLib.instance.proxy().onParticleRegistration((IInfinityParticleType<?>) type)
        );
    }

    @SubscribeEvent
    public void registerMobEffects(RegistryEvent.Register<MobEffect> event) {
        // Register mob effects
        this.registerObjects(RegistryInitializer.Type.MOB_EFFECT, event.getRegistry());
    }

    @SubscribeEvent
    public void registerMenuTypes(RegistryEvent.Register<MenuType<?>> event) {
        // Register menu types
        this.registerObjects(RegistryInitializer.Type.MENU_TYPE, event.getRegistry(), menuType -> {
            if (menuType instanceof IInfinityContainerMenuType) {
                InfinityLib.instance.proxy().registerGuiContainer((IInfinityContainerMenuType) menuType);
            }
        });
    }

    @SubscribeEvent
    public void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        // Register recipe serializers
        this.registerObjects(RegistryInitializer.Type.RECIPE, event.getRegistry(), recipe -> {
            if (recipe instanceof IInfRecipeSerializer) {
                // Also register the recipe's ingredient serializers
                ((IInfRecipeSerializer<?>) recipe).getIngredientSerializers().forEach(ser -> RecipeSerializers.getInstance().registerSerializer(ser));
            }
        });
        // Register ingredient serializers
        if (mod.getModRecipeSerializerRegistry() != null) {
            ReflectionHelper.forEachValueIn(mod.getModRecipeSerializerRegistry(), IInfIngredientSerializer.class, ser -> RecipeSerializers.getInstance().registerSerializer(ser));
        }
    }

    @SubscribeEvent
    public void registerLootModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        // Register global loot modifier serializers
        this.registerObjects(RegistryInitializer.Type.LOOT, event.getRegistry());
    }
}

