package com.infinityraider.infinitylib.proxy.base;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.capability.CapabilityHandler;
import com.infinityraider.infinitylib.capability.ICapabilityImplementation;
import com.infinityraider.infinitylib.config.ConfigurationHandler;
import com.infinityraider.infinitylib.modules.dynamiccamera.IDynamicCameraController;
import com.infinityraider.infinitylib.sound.SidedSoundDelegate;
import com.infinityraider.infinitylib.sound.SoundDelegateServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.server.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface IProxyBase<C extends ConfigurationHandler.SidedModConfig> {
    /**
     * Called to register the mod config
     */
    Function<ForgeConfigSpec.Builder, C> getConfigConstructor();

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

    /**
     * Called to register event handlers for FML IModBusEvent events
     * @param bus the bus for the mod
     */
    default void registerFMLEventHandlers(IEventBus bus) {}

    /** Registers an event handler */
    default void registerEventHandler(Object handler) {
        InfinityLib.instance.getLogger().debug("Registering event handler: " + handler.getClass().getName());
        MinecraftForge.EVENT_BUS.register(handler);
    }

    /** Registers a capability */
    @SuppressWarnings("unchecked")
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

    default void onServerStartingEvent(final ServerStartingEvent event) {};

    default void onServerAboutToStartEvent(final ServerAboutToStartEvent event) {};

    default void onServerStoppingEvent(final ServerStoppingEvent event) {};

    default void onServerStoppedEvent(final ServerStoppedEvent event) {};

    /**
     * ---------------
     * UTILITY METHODS
     * ---------------
     */

    /**
     * @return The physical side, is always Side.SERVER on the server and Side.CLIENT on the client
     */
    default Dist getPhysicalSide() {
        return FMLEnvironment.dist;
    }

    /**
     * @return The effective side, on the server, this is always Side.SERVER, on the client it is dependent on the thread
     */
    default LogicalSide getLogicalSide() {
        return EffectiveSide.get();
    }

    /**
     * @return The minecraft server instance
     */
    default MinecraftServer getMinecraftServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    /**
     * @return the instance of the EntityPlayer on the client, null on the server
     */
    Player getClientPlayer();

    /**
     * @return the client World object on the client, null on the server
     */
    Level getClientWorld();

    /**
     *  @return  the entity in that World object with that id
     */
    default Entity getEntityById(Level world, int id) {
        return world == null ? null : world.getEntity(id);
    }

    /**
     *  @return  the entity in that World object with that id
     */
    default Entity getEntityById(ResourceKey<Level> dimension, int id) {
        return this.getEntityById(this.getWorldFromDimension(dimension), id);
    }

    /**
     *  @return the render view entity on the client, null on the server
     */
    @Nullable
    default Entity getRenderViewEntity() {
        return null;
    }

    /**
     *  Sets the render view entity on the client
     */
    default void setRenderViewEntity(Entity entity) {}

    /**
     *  @return the World object ofr a given dimension key
     */
    Level getWorldFromDimension(ResourceKey<Level> dimension);

    /**
     *  @return the sound delegate for the relevant side
     */
    default SidedSoundDelegate getSoundDelegate() {
        return new SoundDelegateServer();
    }

    /**
     * @return the fov setting on the client, 70 on the server
     */
    default double getFieldOfView() {
        return 70;
    }

    /** Queues a task to be executed on this side */
    default void queueTask(Runnable task) {
        this.getMinecraftServer().submit(new TickTask(this.getMinecraftServer().getTickCount() + 1, task));
    }

    /**
     * Enables or disables the dynamic camera for the given controller, only works client side
     *
     * @param controller the controller
     * @param status true to enable, false to disable
     * @return true if successful
     */
    default boolean toggleDynamicCamera(IDynamicCameraController controller, boolean status) {
        return false;
    }
}
