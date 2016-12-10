package com.infinityraider.infinitylib;

import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.network.NetworkWrapper;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.infinitylib.utility.InfinityLogger;
import com.infinityraider.infinitylib.utility.ModEventHandlerHack;
import com.infinityraider.infinitylib.utility.ModHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This interface should be implemented in a mod's main class to have the registering of Items, Blocks, Renderers, etc. handled by InfinityLib
 * When implementing this interface, the mod must also be annotated with @InfinityMod
 */
@SuppressWarnings("unused")
public abstract class InfinityMod {
    private final InfinityLogger logger;
    private final INetworkWrapper networkWrapper;

    public InfinityMod() {
        this.logger = new InfinityLogger(this);
        this.networkWrapper = new NetworkWrapper(this);
        ModEventHandlerHack.doHack(this);   //you ain't seen nothing
    }

    public final InfinityLogger getLogger() {
        return this.logger;
    }

    public final INetworkWrapper getNetworkWrapper() {
        return this.networkWrapper;
    }

    /**
     * @return The sided proxy object for this mod
     */
    public abstract IProxyBase proxy();

    /**
     * @return The mod ID of the mod
     */
    public abstract String getModId();

    /**
     * Used to register the Blocks, recipes, renderers, TileEntities, etc. for all the mod's blocks.
     * The object returned by this should have a field for each of its blocks
     * @return Block registry object or class
     */
    public abstract Object getModBlockRegistry();

    /**
     * Used to register the Items, recipes, renderers, etc. for all the mod's items.
     * The object returned by this should have a field for each of its items
     * @return Item registry object or class
     */
    public abstract Object getModItemRegistry();

    /**
     * Used to register the Entities for all the mod's entities.
     * The object returned by this should have a field for each of its entities
     * @return Entity registry object or class
     */
    public abstract Object getModEntityRegistry();

    /**
     * Register all messages added by this mod
     * @param wrapper NetworkWrapper instance to register messages to
     */
    public abstract void registerMessages(INetworkWrapper wrapper);


    /**
     * ----------------------------
     * FML Mod loading cycle events
     * ----------------------------
     */

    @Mod.EventHandler
    public final void preInit(FMLPreInitializationEvent event) {
        this.getLogger().debug("Starting Pre-Initialization");
        proxy().initConfiguration(event);
        proxy().preInitStart(event);
        proxy().activateRequiredModules();
        ModHelper.getInstance().RegisterBlocksAndItems(this);
        InfinityLib.proxy.registerRenderers(this);
        InfinityLib.proxy.registerEntities(this);
        proxy().preInitEnd(event);
        this.getLogger().debug("Pre-Initialization Complete");
    }

    @Mod.EventHandler
    public final void init(FMLInitializationEvent event) {
        this.getLogger().debug("Starting Initialization");
        proxy().initStart(event);
        proxy().registerCapabilities();
        proxy().registerEventHandlers();
        registerMessages(this.getNetworkWrapper());
        ModHelper.getInstance().registerRecipes(this);
        proxy().initEnd(event);
        this.getLogger().debug("Initialization Complete");
    }

    @Mod.EventHandler
    public final void postInit(FMLPostInitializationEvent event) {
        this.getLogger().debug("Starting Post-Initialization");
        proxy().postInitStart(event);
        proxy().postInitEnd(event);
        this.getLogger().debug("Post-Initialization Complete");
    }

    @Mod.EventHandler
    public final void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        proxy().onServerAboutToStart(event);
    }

    @Mod.EventHandler
    public final void onServerStarting(FMLServerStartingEvent event) {
        proxy().onServerStarting(event);
    }

    @Mod.EventHandler
    public final void onServerStarted(FMLServerStartedEvent event) {
        proxy().onServerStarted(event);
    }

    @Mod.EventHandler
    public final void onServerStopping(FMLServerStoppingEvent event) {
        proxy().onServerStopping(event);
    }

    @Mod.EventHandler
    public final void onServerStopped(FMLServerStoppedEvent event) {
        proxy().onServerStopped(event);
    }


    /**
     * --------------------------
     * Proxy utility method calls
     * --------------------------
     */

    /**
     * @return The physical side, is always Side.SERVER on the server and Side.CLIENT on the client
     */
    public final Side getPhysicalSide() {
        return this.proxy().getPhysicalSide();
    }

    /**
     * @return The effective side, on the server, this is always Side.SERVER, on the client it is dependent on the thread
     */
    public final Side getEffectiveSide() {
        return this.proxy().getEffectiveSide();
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
    public final EntityPlayer getClientPlayer() {
        return this.proxy().getClientPlayer();
    }

    /**
     * @return the client World object on the client, null on the server
     */
    public final World getClientWorld() {
        return this.proxy().getClientWorld();
    }

    /**
     * Returns the World object corresponding to the dimension id
     * @param dimension dimension id
     * @return world object
     */
    public final World getWorldByDimensionId(int dimension) {
        return this.proxy().getWorldByDimensionId(dimension);
    }

    /**
     * Returns the entity in that dimension with that id
     * @param dimension dimension id
     * @param id entity id
     * @return the entity
     */
    public final Entity getEntityById(int dimension, int id) {
        return this.proxy().getEntityById(dimension, id);
    }

    /**
     *  @return  the entity in that World object with that id
     */
    public final Entity getEntityById(World world, int id) {
        return this.proxy().getEntityById(world, id);
    }

    /** Queues a task to be executed on this side */
    public final void queueTask(Runnable task) {
        this.proxy().queueTask(task);
    }
}
