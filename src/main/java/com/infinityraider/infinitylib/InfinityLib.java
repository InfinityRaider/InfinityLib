package com.infinityraider.infinitylib;

import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.proxy.IProxyBase;
import com.infinityraider.infinitylib.reference.Reference;
import com.infinityraider.infinitylib.utility.LogHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

@InfinityMod
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class InfinityLib implements IInfinityMod {

    @Mod.Instance(Reference.MOD_ID)
    public static InfinityLib instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxyBase proxy;

    @Override
    public String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    public Object getModBlockRegistry() {
        return this;
    }

    @Override
    public Object getModItemRegistry() {
        return this;
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {

    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void preInit(FMLPreInitializationEvent event) {
        LogHelper.debug("Starting Pre-Initialization");
        proxy.preInit(event);
        LogHelper.debug("Pre-Initialization Complete");
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void init(FMLInitializationEvent event) {
        LogHelper.debug("Starting Initialization");
        proxy.init(event);
        LogHelper.debug("Initialization Complete");
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void postInit(FMLPostInitializationEvent event) {
        LogHelper.debug("Starting Post-Initialization");
        proxy.postInit(event);
        LogHelper.debug("Post-Initialization Complete");
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        proxy.onServerAboutToStart(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void onServerStarting(FMLServerStartingEvent event) {
        proxy.onServerStarting(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void onServerStarted(FMLServerStartedEvent event) {
        proxy.onServerStarted(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void onServerStopping(FMLServerStoppingEvent event) {
        proxy.onServerStopping(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void onServerStopped(FMLServerStoppedEvent event) {
        proxy.onServerStopped(event);
    }
}