package com.infinityraider.infinitylib;

import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.proxy.IProxy;
import com.infinityraider.infinitylib.reference.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class InfinityLib extends InfinityMod {

    @Mod.Instance(Reference.MOD_ID)
    public static InfinityLib instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Override
    public IProxy proxy() {
        return proxy;
    }

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
    public Object getModEntityRegistry() {
        return this;
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        for(Module module : Module.getActiveModules()) {
            module.registerMessages(wrapper);
        }
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void preInitMod(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void initMod(FMLInitializationEvent event) {
        super.init(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void postInitMod(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onServerAboutToStartCallBack(FMLServerAboutToStartEvent event) {
        super.onServerAboutToStart(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onServerStartingCallBack(FMLServerStartingEvent event) {
        super.onServerStarting(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onServerStartedCallBack(FMLServerStartedEvent event) {
        super.onServerStarted(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onServerStoppingCallBack(FMLServerStoppingEvent event) {
        super.onServerStopping(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onServerStoppedCallBack(FMLServerStoppedEvent event) {
        super.onServerStopped(event);
    }
}