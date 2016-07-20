package com.infinityraider.infinitylib;

import com.infinityraider.infinitylib.network.INetworkWrapper;

/**
 * This interface should be implemented in a mod's main class to have the registering of Items, Blocks, Renderers, etc. handled by InfinityLib
 * When implementing this interface, the mod must also be annotated with @InfinityMod
 */
public interface IInfinityMod {
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

}
