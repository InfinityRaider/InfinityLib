package com.infinityraider.infinitylib.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public interface IProxy {
    /**
     * @return The physical side, is always Side.SERVER on the server and Side.CLIENT on the client
     */
    Side getPhysicalSide();

    /**
     * @return The effective side, on the server, this is always Side.SERVER, on the client it is dependent on the thread
     */
    Side getEffectiveSide();

    /**
     * Performs all needed operations for the proxy's side during FML's pre init stage
     */
    void preInit(FMLPreInitializationEvent event);

    /**
     * Performs all needed operations for the proxy's side during FML's init stage
     */
    void init(FMLInitializationEvent event);

    /**
     * Performs all needed operations for the proxy's side during FML's post init stage
     */
    void postInit(FMLPostInitializationEvent event);

    /**
     * Performs all needed operations for the proxy's side when the server is about to start
     */
    void onServerAboutToStart(FMLServerAboutToStartEvent event);

    /**
     * Performs all needed operations for the proxy's side when the server is starting
     */
    void onServerStarting(FMLServerStartingEvent event);

    /**
     * Performs all needed operations for the proxy's side when the server is started
     */
    void onServerStarted(FMLServerStartedEvent event);

    /**
     * Performs all needed operations for the proxy's side when the server is stopping
     */
    void onServerStopping(FMLServerStoppingEvent event);

    /**
     * Performs all needed operations for the proxy's side when the server is stopped
     */
    void onServerStopped(FMLServerStoppedEvent event);

    /**
     * Registers the relevant event handlers for each side
     */
    void registerEventHandlers();

    /**
     * Initializes the configuration options relevant to the specific side
     * @param event the FMLPreInitialization event passed to SettlerCraft
     */
    void initConfiguration(FMLPreInitializationEvent event);

    /**
     * @return the instance of the EntityPlayer on the client, null on the server
     */
    EntityPlayer getClientPlayer();

    /**
     * @return the client World object on the client, null on the server
     */
    World getClientWorld();

    /**
     * Returns the World object corresponding to the dimension id
     * @param dimension dimension id
     * @return world object
     */
    World getWorldByDimensionId(int dimension);

    /**
     * Returns the entity in that dimension with that id
     * @param dimension dimension id
     * @param id entity id
     * @return the entity
     */
    Entity getEntityById(int dimension, int id);

    /**
     *  @return  the entity in that World object with that id
     */
    Entity getEntityById(World world, int id);

    /** Queues a task to be executed on this side */
    void queueTask(Runnable task);
}
