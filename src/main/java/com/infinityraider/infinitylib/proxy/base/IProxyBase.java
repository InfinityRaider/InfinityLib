package com.infinityraider.infinitylib.proxy.base;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.capability.CapabilityHandler;
import com.infinityraider.infinitylib.capability.ICapabilityImplementation;
import com.infinityraider.infinitylib.config.ConfigurationHandler;
import com.infinityraider.infinitylib.sound.SidedSoundDelegate;
import com.infinityraider.infinitylib.sound.SoundDelegateServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.*;
import net.minecraftforge.fml.loading.FMLEnvironment;

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

    default void onServerStartingEvent(final FMLServerStartingEvent event) {};

    default void onServerAboutToStartEvent(final FMLServerAboutToStartEvent event) {};

    default void onServerStoppingEvent(final FMLServerStoppingEvent event) {};

    default void onServerStoppedEvent(final FMLServerStoppedEvent event) {};

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
    World getWorldFromDimension(RegistryKey<World> dimension);


    /**
     *  @return the sound delegate for the relevant side
     */
    default SidedSoundDelegate getSoundDelegate() {
        return new SoundDelegateServer();
    }

    /** Queues a task to be executed on this side */
    void queueTask(Runnable task);

    /** Sets the ItemStackTileEntityRenderer in the properties on the client side */
    default Item.Properties setItemRenderer(Item.Properties properties) {
        return properties;
    }
}
