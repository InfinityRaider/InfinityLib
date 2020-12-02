package com.infinityraider.infinitylib.proxy.base;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.block.IInfinityBlock;
import com.infinityraider.infinitylib.block.IInfinityBlockWithTile;
import com.infinityraider.infinitylib.capability.CapabilityHandler;
import com.infinityraider.infinitylib.capability.ICapabilityImplementation;
import com.infinityraider.infinitylib.effect.IInfinityEffect;
import com.infinityraider.infinitylib.enchantment.IInfinityEnchantment;
import com.infinityraider.infinitylib.entity.IInfinityEntityType;
import com.infinityraider.infinitylib.entity.IInfinityLivingEntityType;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.sound.IInfinitySoundEvent;
import com.infinityraider.infinitylib.sound.SidedSoundDelegate;
import com.infinityraider.infinitylib.sound.SoundDelegateServer;
import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import com.infinityraider.infinitylib.utility.ReflectionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface IProxyBase {
    /**
     * -------------------
     * REGISTERING METHODS
     * -------------------
     */

    default void registerRegistrables(InfinityMod mod) {
        this.registerBlocks(mod);
        this.registerItems(mod);
        this.registerEnchantments(mod);
        this.registerEntities(mod);
        this.registerSounds(mod);
        this.registerEffects(mod);
    }

    default void registerBlocks(InfinityMod mod) {
        // Register blocks
        this.registerObjects(mod, mod.getModBlockRegistry(), IInfinityBlock.class, ForgeRegistries.BLOCKS, block -> {
            // TileEntity registration:
            if (block instanceof IInfinityBlockWithTile) {
                //TODO: register Tile Entity
            }
        });
    }

    default void registerItems(InfinityMod mod) {
        // Register items
        this.registerObjects(mod, mod.getModItemRegistry(), IInfinityItem.class, ForgeRegistries.ITEMS);
    }

    default void registerEnchantments(InfinityMod mod) {
        // Register enchantments
        this.registerObjects(mod, mod.getModEnchantmentRegistry(), IInfinityEnchantment.class, ForgeRegistries.ENCHANTMENTS);
    }

    default void registerEntities(InfinityMod mod) {
        // Create a deferred item register for the spawn eggs
        DeferredRegister<Item> itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, mod.getModId());
        // Register entities
        this.registerObjects(mod, mod.getModEntityRegistry(), IInfinityEntityType.class, ForgeRegistries.ENTITIES, entityType -> {
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
            }
        });
        itemRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    default void registerSounds(InfinityMod mod) {
        // Register enchantments
        this.registerObjects(mod, mod.getModSoundRegistry(), IInfinitySoundEvent.class, ForgeRegistries.SOUND_EVENTS);

    }

    default void registerEffects(InfinityMod mod) {
        // Register effects
        this.registerObjects(mod, mod.getModEffectRegistry(), IInfinityEffect.class, ForgeRegistries.POTIONS);
    }

    default <T extends IForgeRegistryEntry<T>> void registerObjects(InfinityMod mod, Object modRegistry,
                                                                    Class<? extends IInfinityRegistrable<T>> clazz,
                                                                    IForgeRegistry<T> targetRegistry) {
        this.registerObjects(mod, modRegistry, clazz, targetRegistry, (obj) -> {});
    }

    default <T extends IForgeRegistryEntry<T>> void registerObjects(InfinityMod mod, Object modRegistry,
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

    default <T extends IForgeRegistryEntry<T>> void registerObject(InfinityMod mod, IInfinityRegistrable<T> object,
                                                                   DeferredRegister<T> register,
                                                                   Consumer<IInfinityRegistrable<T>> tasks) {
        if(object.isEnabled()) {
            mod.getLogger().debug(" - Registering: " + object.getInternalName());
            register.register(object.getInternalName(), object::cast);
            tasks.accept(object);
        }
    }

    /**
     * Called to register the event handlers
     */
    void registerEventHandlers();

    /**
     * Called to activate all the necessary InfinityLib modules for this mod
     */
    void activateRequiredModules();

    /**
     * Called to register the capabilities for this mod
     */
    void registerCapabilities();

    /** Registers an event handler */
    default void registerEventHandler(Object handler) {
        MinecraftForge.EVENT_BUS.register(handler);
    }


    /** Registers a capability */
    default void registerCapability(ICapabilityImplementation capability) {
        CapabilityHandler.getInstance().registerCapability(capability);
    }


    /**
     * -----------------------------
     * FML MOD LOADING CYCLE METHODS
     * -----------------------------
     */
    default void onCommonSetupEvent(final FMLCommonSetupEvent event) {}

    default void onClientSetupEvent(final FMLClientSetupEvent event) {}

    default void onDedicatedServerSetupEvent(final FMLDedicatedServerSetupEvent event) {}

    default void onInterModEnqueueEvent(final InterModEnqueueEvent event) {}

    default void onInterModProcessEvent(final InterModProcessEvent event) {}

    default void onModLoadCompleteEvent(final FMLLoadCompleteEvent event) {}

    /**
     * ---------------
     * UTILITY METHODS
     * ---------------
     */

    /**
     * @return The physical side, is always Side.SERVER on the server and Side.CLIENT on the client
     */
    LogicalSide getPhysicalSide();

    /**
     * @return The effective side, on the server, this is always Side.SERVER, on the client it is dependent on the thread
     */
    default LogicalSide getEffectiveSide() {
        return EffectiveSide.get();
    }

    /**
     * @return The minecraft server instance
     */
    default MinecraftServer getMinecraftServer() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

    /**
     * @return the instance of the EntityPlayer on the client, null on the server
     */
    PlayerEntity getClientPlayer();

    /**
     * @return the client World object on the client, null on the server
     */
    World getClientWorld();

    /**
     *  @return  the entity in that World object with that id
     */
    default Entity getEntityById(World world, int id) {
        return world == null ? null : world.getEntityByID(id);
    }

    /**
     *  @return  the entity in that World object with that id
     */
    default Entity getEntityById(RegistryKey<World> dimension, int id) {
        return this.getEntityById(this.getWorldFromDimension(dimension), id);
    }

    /**
     *  @return the World object ofr a given dimension key
     */
    World getWorldFromDimension(RegistryKey<World> dimension);


    /**
     *  @return the sound delegate for the relevant side
     */
    default SidedSoundDelegate getSoundDelegate() {
        return new SoundDelegateServer();
    }

    /** Queues a task to be executed on this side */
    void queueTask(Runnable task);
}
