package com.infinityraider.infinitylib;

import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.proxy.IProxyBase;
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
    IProxyBase proxy();

    /**
     * @return The mod ID of the mod
     */
    String getModId();

    /**
     * Used to register the Blocks, recipes, renderers, TileEntities, etc. for all the mod's blocks.
     * The object returned by this should have a field for each of its blocks
     * @return Block registry object or class
     */
    Object getModBlockRegistry();

    /**
     * Used to register the Items, recipes, renderers, etc. for all the mod's items.
     * The object returned by this should have a field for each of its items
     * @return Item registry object or class
     */
    Object getModItemRegistry();

    /**
     * Register all messages added by this mod
     * @param wrapper NetworkWrapper instance to register messages to
     */
    void registerMessages(INetworkWrapper wrapper);


    default void preInit(FMLPreInitializationEvent event) {
        LogHelper.debug("Starting Pre-Initialization");
        proxy().preInitStart(event);
        ModHelper.getInstance().RegisterBlocksAndItems(this);
        proxy().preInitEnd(event);
        LogHelper.debug("Pre-Initialization Complete");
    }

    default void init(FMLInitializationEvent event) {
        LogHelper.debug("Starting Initialization");
        proxy().initStart(event);
        ModHelper.getInstance().registerRecipes(this);
        proxy().initEnd(event);
        LogHelper.debug("Initialization Complete");
    }

    default void postInit(FMLPostInitializationEvent event) {
        LogHelper.debug("Starting Post-Initialization");
        proxy().postInitStart(event);
        proxy().postInitEnd(event);
        LogHelper.debug("Post-Initialization Complete");
    }

    default void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        proxy().onServerAboutToStart(event);
    }

    default void onServerStarting(FMLServerStartingEvent event) {
        proxy().onServerStarting(event);
    }

    default void onServerStarted(FMLServerStartedEvent event) {
        proxy().onServerStarted(event);
    }

    default void onServerStopping(FMLServerStoppingEvent event) {
        proxy().onServerStopping(event);
    }

    default void onServerStopped(FMLServerStoppedEvent event) {
        proxy().onServerStopped(event);
    }
}
