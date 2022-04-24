package com.infinityraider.infinitylib;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.config.ConfigurationHandler;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.network.NetworkWrapper;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.infinitylib.render.model.InfModelLoader;
import com.infinityraider.infinitylib.utility.InfinityLogger;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistrationHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.List;

/**
 * This interface should be implemented in a mod's main class to have the registering of Items, Blocks, Renderers, etc. handled by InfinityLib
 * When implementing this interface, the mod must also be annotated with @InfinityMod
 */
public abstract class InfinityMod<P extends IProxyBase<C>, C extends ConfigurationHandler.SidedModConfig> {
    private final InfinityLogger logger;
    private final NetworkWrapper networkWrapper;
    private final P proxy;
    private final ConfigurationHandler<C> config;

    @SuppressWarnings("Unchecked")
    public InfinityMod() {
        //Populate static mod instance
        this.onModConstructed();
        //Create logger
        this.logger = new InfinityLogger(this);
        // Create network wrapper
        this.networkWrapper = new NetworkWrapper(this);
        // Create proxy
        this.proxy = this.createProxy();
        // Create configuration
        this.config = new ConfigurationHandler<>(ModLoadingContext.get(), this.proxy().getConfigConstructor(), this);
        // Register FML mod loading cycle listeners
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        IEventBus bus = context.getModEventBus();
        bus.addListener(this::onCommonSetupEvent);
        bus.addListener(this::onClientSetupEvent);
        bus.addListener(this::onDedicatedServerSetupEvent);
        bus.addListener(this::onInterModEnqueueEvent);
        bus.addListener(this::onInterModProcessEvent);
        bus.addListener(this::onModLoadCompleteEvent);
        this.proxy().registerModBusEventHandlers(bus);
        MinecraftForge.EVENT_BUS.register(this);
        //Activate required modules
        this.proxy().activateRequiredModules();
        // Call for automatic registration of IInfinityRegistreables
        bus.register(new RegistrationHandler(this));
        // Call for automatic registration of renderers and other
        InfinityLib.instance.proxy().registerRegistrables(this);
        // Register capabilities
        this.proxy().registerCapabilities();
        // Initialize the API
        this.initializeAPI();
    }

    private P createProxy() {
        P proxy = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> this::createClientProxy);
        if (proxy == null) {
            proxy = DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () -> this::createServerProxy);
        }
        if (proxy == null) {
            // Can only happen if the mod fails to correctly implement the createClientProxy and/or the createServerProxy methods
            throw new RuntimeException("Failed to create SidedProxy for mod " + this.getModId() + " on side: " + FMLEnvironment.dist.name());
        }
        return proxy;
    }

    private void init() {
        // Register event handlers
        this.proxy().registerEventHandlers();
        // Register messages
        this.networkWrapper.init();
    }

    private void initClient() {
        // Register renderers
        InfinityLib.instance.proxy().registerRenderers(this);
    }

    public final InfinityLogger getLogger() {
        return this.logger;
    }

    public final INetworkWrapper getNetworkWrapper() {
        return this.networkWrapper;
    }

    public final P proxy() {
        return this.proxy;
    }

    public final C getConfig() {
        return this.config.getConfig();
    }

    /**
     * @return The mod ID of the mod
     */
    public abstract String getModId();

    /**
     * Provides access to the instantiated mod object, for instance to store it in a static field
     */
    protected abstract void onModConstructed();

    /**
     * @return Creates the client proxy object for this mod
     */
    @OnlyIn(Dist.CLIENT)
    protected abstract P createClientProxy();

    /**
     * @return Creates the server proxy object for this mod
     */
    @OnlyIn(Dist.DEDICATED_SERVER)
    protected abstract P createServerProxy();

    /**
     * Register all messages added by this mod
     * @param wrapper NetworkWrapper instance to register messages to
     */
    public void registerMessages(INetworkWrapper wrapper) {}

    /**
     * Use to initialize the mod API
     */
    public void initializeAPI() {}

    /**
     * Used to register all of the mod's Blocks.
     * The object returned by this should have a field for each of its Blocks
     *
     * Note: for this to work, the Blocks must implement IInfinityBlock
     *
     * @return Block registry object or class
     */
    public ModContentRegistry getModBlockRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's TileEntity Types.
     * The object returned by this should have a field for each of its TileEntity Types
     *
     * Note: for this to work, the TileEntity Types must implement IInfinityTileEntityType
     *
     * @return TileEntity registry object or class
     */
    public ModContentRegistry getModTileRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's Items.
     * The object returned by this should have a field for each of its Items
     *
     * Note: for this to work, the Items must implement IInfinityItem
     *
     * @return Item registry object or class
     */
    public ModContentRegistry getModItemRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's Fluids.
     * The object returned by this should have a field for each of its Fluids
     *
     * Note: for this to work, the Fluids must implement IInfinityFluid
     *
     * @return Fluid registry object or class
     */
    public ModContentRegistry getModFluidRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's Biomes.
     * The object returned by this should have a field for each of its Biomes
     *
     * Note: currently not implemented, will not work
     *
     * @return Biome registry object or class
     */
    public ModContentRegistry getModBiomeRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's Enchantments.
     * The object returned by this should have a field for each of its Enchantments
     *
     * Note: for this to work, the Enchantments must implement IInfinityEnchantment
     *
     * @return Enchantment registry object or class
     */
    public  ModContentRegistry getModEnchantmentRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's Entity Types.
     * The object returned by this should have a field for each of its Entity Types
     *
     * Note: for this to work, the Entity Types must implement IInfinityEntityType
     *
     * @return Entity registry object or class
     */
    public ModContentRegistry getModEntityRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's Effects.
     * The object returned by this should have a field for each of its Effects
     *
     * Note: for this to work, the Effects must implement IInfinityEffect
     *
     * @return Potion registry object or class
     */
    public ModContentRegistry getModMobEffectRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's PotionTypes.
     * The object returned by this should have a field for each of its PotionTypes
     *
     * Note: currently not implemented, will not work
     *
     * @return PotionType registry object or class
     */
    public ModContentRegistry getModPotionRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's SoundEvents.
     * The object returned by this should have a field for each of its SoundEvents
     *
     * Note: for this to work, the SoundEvents must implement IInfinitySoundEvent
     *
     * @return SoundEvent registry object or class
     */
    public ModContentRegistry getModSoundRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's ParticleTypes.
     * The object returned by this should have a field for each of its ParticleTypes
     *
     * Note: for this to work, the ParticleTypes must implement IInfinityParticleType
     *
     * @return ParticleType registry object or class
     */
    public ModContentRegistry getModParticleRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's ContainerTypes.
     * The object returned by this should have a field for each of its ContainerTypes
     *
     * Note: for this to work, the ContainerTypes must implement IInfinityContainerType
     *
     * @return ContainerType registry object or class
     */
    public ModContentRegistry getModContainerRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's Recipe and/or Ingredient Serializers.
     * The object returned by this should have a field for each of its Recipe and/or Ingredient Serializers
     *
     * Note: for this to work, the Recipe and/or Ingredient Serializers must implement IInfRecipeSerializer
     *       and IInfIngredientSerializer respectively.
     *
     * @return IRecipeSerializer registry object or class
     */
    public ModContentRegistry getModRecipeSerializerRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's Global Loot Modifier Serializers
     * The object returned by this should have a field for each of its Global Loot Modifier Serializers
     *
     * Note: for this to work, the Global Loot Modifier Serializers must implement IInfLootModifierSerializer
     *
     * @return IRecipeSerializer registry object or class
     */
    public ModContentRegistry getModLootModifierSerializerRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's Structures.
     * The object returned by this should have a field for each of its Structures
     *
     * @return Structure registry object
     */
    // TODO: rework
    public Class<?> getStructureRegistry() {
        return null;
    }

    /**
     * Used to register all of the mod's VillagerProfessions.
     * The object returned by this should have a field for each of its VillagerProfessions
     *
     * Note: currently not implemented, will not work
     *
     * @return VillagerProfession registry object or class
     */
    // TODO: rework
    public Class<?> getModVillagerProfessionRegistry() {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public List<InfModelLoader<?>> getModModelLoaders() {
        return ImmutableList.of();
    }

    /**
     * --------------------------
     * FML Mod Loading Listeners
     * --------------------------
     */

    public final void onCommonSetupEvent(final FMLCommonSetupEvent event) {
        //self init
        this.init();
        //forward to proxy
        this.proxy().onCommonSetupEvent(event);
    }

    public final void onClientSetupEvent(final FMLClientSetupEvent event) {
        //self init
        this.initClient();
        //forward to proxy
        this.proxy().onClientSetupEvent(event);}

    public final void onDedicatedServerSetupEvent(final FMLDedicatedServerSetupEvent event) {
        //forward to proxy
        this.proxy().onDedicatedServerSetupEvent(event);
    }

    public final void onInterModEnqueueEvent(final InterModEnqueueEvent event) {
        //forward to proxy
        this.proxy().onInterModEnqueueEvent(event);
    }

    public final void onInterModProcessEvent(final InterModProcessEvent event) {
        //forward to proxy
        this.proxy().onInterModProcessEvent(event);
    }

    public final void onModLoadCompleteEvent(final FMLLoadCompleteEvent event) {
        //forward to proxy
        this.proxy().onModLoadCompleteEvent(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public final void onServerStartingEvent(final ServerStartingEvent event) {
        //forward to proxy
        this.proxy().onServerStartingEvent(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public final void onServerAboutToStartEvent(final ServerAboutToStartEvent event) {
        //forward to proxy
        this.proxy().onServerAboutToStartEvent(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public final void onServerStoppingEvent(final ServerStoppingEvent event) {
        //forward to proxy
        this.proxy().onServerStoppingEvent(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public final void onServerStoppedEvent(final ServerStoppedEvent event) {
        //forward to proxy
        this.proxy().onServerStoppedEvent(event);
    }

    /**
     * --------------------------
     * Proxy utility method calls
     * --------------------------
     */

    /**
     * @return The physical side, is always Side.SERVER on the server and Side.CLIENT on the client
     */
    public final Dist getPhysicalSide() {
        return this.proxy().getPhysicalSide();
    }

    /**
     * @return The effective side, on the server, this is always Side.SERVER, on the client it is dependent on the thread
     */
    public final LogicalSide getEffectiveSide() {
        return this.proxy().getLogicalSide();
    }

    /**
     * @return The minecraft server instance
     */
    public final MinecraftServer getMinecraftServer() {
        return this.proxy().getMinecraftServer();
    }

    /**
     * @return the instance of the EntityPlayer on the client, null on the server
     */
    public final Player getClientPlayer() {
        return this.proxy().getClientPlayer();
    }

    /**
     * @return the client World object on the client, null on the server
     */
    public final Level getClientWorld() {
        return this.proxy().getClientWorld();
    }

    /**
     * @return the client World object on the client, null on the server
     */
    public final Level getWorldFromDimension(ResourceKey<Level> dimension) {
        return this.proxy().getWorldFromDimension(dimension);
    }

    /**
     *  @return  the entity in that World object with that id
     */
    public final Entity getEntityById(Level world, int id) {
        return this.proxy().getEntityById(world, id);
    }

    /**
     *  @return  the entity in that World object with that id
     */
    public final Entity getEntityById(ResourceKey<Level> dimension, int id) {
        return this.proxy().getEntityById(dimension, id);
    }

    /** Queues a task to be executed on this side */
    public final void queueTask(Runnable task) {
        this.proxy().queueTask(task);
    }

    /** Registers an event handler */
    public final void registerEventHandler(Object handler) {
        this.proxy().registerEventHandler(handler);
    }
}
