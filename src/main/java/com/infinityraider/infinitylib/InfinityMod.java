package com.infinityraider.infinitylib;

import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.infinitylib.utility.LogHelper;
import com.infinityraider.infinitylib.utility.ModHelper;
import net.minecraftforge.fml.common.event.*;

/**
 * This interface should be implemented in a mod's main class to have the registering of Items, Blocks, Renderers, etc. handled by InfinityLib
 * When implementing this interface, the mod must also be annotated with @InfinityMod
 */
public abstract class InfinityMod {
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

    public final void preInit(FMLPreInitializationEvent event) {
        LogHelper.debug("Starting Pre-Initialization");
        proxy().preInitStart(event);
        ModHelper.getInstance().RegisterBlocksAndItems(this);
        InfinityLib.proxy.registerRenderers(this);
        InfinityLib.proxy.registerEntities(this);
        proxy().preInitEnd(event);
        LogHelper.debug("Pre-Initialization Complete");
    }

    public final void init(FMLInitializationEvent event) {
        LogHelper.debug("Starting Initialization");
        proxy().initStart(event);
        ModHelper.getInstance().registerRecipes(this);
        proxy().initEnd(event);
        LogHelper.debug("Initialization Complete");
    }

    public final void postInit(FMLPostInitializationEvent event) {
        LogHelper.debug("Starting Post-Initialization");
        proxy().postInitStart(event);
        proxy().postInitEnd(event);
        LogHelper.debug("Post-Initialization Complete");
    }

    public final void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        proxy().onServerAboutToStart(event);
    }

    public final void onServerStarting(FMLServerStartingEvent event) {
        proxy().onServerStarting(event);
    }

    public final void onServerStarted(FMLServerStartedEvent event) {
        proxy().onServerStarted(event);
    }

    public final void onServerStopping(FMLServerStoppingEvent event) {
        proxy().onServerStopping(event);
    }

    public final void onServerStopped(FMLServerStoppedEvent event) {
        proxy().onServerStopped(event);
    }
}
