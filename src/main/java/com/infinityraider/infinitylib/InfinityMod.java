package com.infinityraider.infinitylib;

import com.infinityraider.infinitylib.config.ConfigurationHandler;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.network.NetworkWrapper;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.infinitylib.utility.InfinityLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

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
        this.config = new ConfigurationHandler<>(ModLoadingContext.get(), this.proxy().getConfigConstructor());
        // Register FML mod loading cycle listeners
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        IEventBus bus = context.getModEventBus();
        bus.addListener(this::onCommonSetupEvent);
        bus.addListener(this::onClientSetupEvent);
        bus.addListener(this::onDedicatedServerSetupEvent);
        bus.addListener(this::onInterModEnqueueEvent);
        bus.addListener(this::onInterModProcessEvent);
        bus.addListener(this::onModLoadCompleteEvent);
        MinecraftForge.EVENT_BUS.register(this);
        //Activate required modules
        this.proxy().activateRequiredModules();
        // Call for deferred, automatic registration of IInfinityRegistrable objects
        this.proxy().registerRegistrables(this);
        // Register event handlers
        this.proxy().registerEventHandlers();
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
        // Register capabilities
        this.proxy().registerCapabilities();
        // Register messages
        this.networkWrapper.init();
    }

    private void initClient() {
        // Register renderers
        this.proxy().registerRenderers(this);
    }

    public final InfinityLogger getLogger() {
        return this.logger;
    }

    public final INetworkWrapper getNetworkWrapper() {
        return this.networkWrapper;
    }

    public P proxy() {
        return this.proxy;
    }

    public C getConfig() {
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
     * Used to register the Blocks, recipes, renderers, TileEntities, etc. for all the mod's blocks.
     * The object returned by this should have a field for each of its blocks
     * @return Block registry object or class
     */
    public Object getModBlockRegistry() {
        return null;
    }

    /**
     * Used to register the Items, recipes, renderers, etc. for all the mod's items.
     * The object returned by this should have a field for each of its items
     * @return Item registry object or class
     */
    public Object getModItemRegistry() {
        return null;
    }

    /**
     * Used to register the Biomes for all the mod's biomes.
     * The object returned by this should have a field for each of its biomes
     * @return Biome registry object or class
     */
    public Object getModBiomeRegistry() {
        return null;
    }

    /**
     * Used to register the Enchantments for all the mod's enchantments.
     * The object returned by this should have a field for each of its enchantments
     * @return Enchantment registry object or class
     */
    public  Object getModEnchantmentRegistry() {
        return null;
    }

    /**
     * Used to register the Entities for all the mod's entities.
     * The object returned by this should have a field for each of its entities
     * @return Entity registry object or class
     */
    public  Object getModEntityRegistry() {
        return null;
    }

    /**
     * Used to register the Potions for all the mod's potions.
     * The object returned by this should have a field for each of its Potions
     * @return Potion registry object or class
     */
    public Object getModEffectRegistry() {
        return null;
    }

    /**
     * Used to register the PotionTypes for all the mod's potion type.
     * The object returned by this should have a field for each of its PotionTypes
     * @return PotionType registry object or class
     */
    public Object getModPotionTypeRegistry() {
        return null;
    }

    /**
     * Used to register the Sounds for all the mod's sounds.
     * The object returned by this should have a field for each of its SoundEvents
     * @return SoundEvent registry object or class
     */
    public Object getModSoundRegistry() {
        return null;
    }

    /**
     * Used to register the VillagerProfessions for all the mod's villager professions.
     * The object returned by this should have a field for each of its VillagerProfessions
     * @return VillagerProfession registry object or class
     */
    public Object getModVillagerProfessionRegistry() {
        return null;
    }



    /**
     * --------------------------
     * FML Mod Loading Listeners
     * --------------------------
     */

    private void onCommonSetupEvent(final FMLCommonSetupEvent event) {
        //self init
        this.init();
        //forward to proxy
        this.proxy().onCommonSetupEvent(event);
    }

    private void onClientSetupEvent(final FMLClientSetupEvent event) {
        //self init
        this.initClient();
        //forward to proxy
        this.proxy().onClientSetupEvent(event);}

    private void onDedicatedServerSetupEvent(final FMLDedicatedServerSetupEvent event) {
        //forward to proxy
        this.proxy().onDedicatedServerSetupEvent(event);
    }

    private void onInterModEnqueueEvent(final InterModEnqueueEvent event) {
        //forward to proxy
        this.proxy().onInterModEnqueueEvent(event);
    }

    private void onInterModProcessEvent(final InterModProcessEvent event) {
        //forward to proxy
        this.proxy().onInterModProcessEvent(event);
    }

    private void onModLoadCompleteEvent(final FMLLoadCompleteEvent event) {
        //forward to proxy
        this.proxy().onModLoadCompleteEvent(event);
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
    public final PlayerEntity getClientPlayer() {
        return this.proxy().getClientPlayer();
    }

    /**
     * @return the client World object on the client, null on the server
     */
    public final World getClientWorld() {
        return this.proxy().getClientWorld();
    }

    /**
     * @return the client World object on the client, null on the server
     */
    public final World getWorldFromDimension(RegistryKey<World> dimension) {
        return this.proxy().getWorldFromDimension(dimension);
    }

    /**
     *  @return  the entity in that World object with that id
     */
    public final Entity getEntityById(World world, int id) {
        return this.proxy().getEntityById(world, id);
    }

    /**
     *  @return  the entity in that World object with that id
     */
    public final Entity getEntityById(RegistryKey<World> dimension, int id) {
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
