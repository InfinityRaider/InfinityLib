package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.handler.ConfigurationHandler;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.infinitylib.utility.ModHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy extends IProxyBase {
    default void registerEntities(InfinityMod mod) {
        ModHelper.getInstance().registerEntities(mod);
    }

    @Override
    default void initEnd(FMLInitializationEvent event) {
        Module.getActiveModules().forEach(Module::init);
    }
    @Override
    default void postInitEnd(FMLPostInitializationEvent event) {
        Module.getActiveModules().forEach(Module::postInit);
    }

    @Override
    default void initConfiguration(FMLPreInitializationEvent event) {
        ConfigurationHandler.getInstance().init(event);
    }

    void registerRenderers(InfinityMod mod);

    @Override
    default void registerEventHandlers() {
        for(Module module : Module.getActiveModules()) {
            module.getCommonEventHandlers().forEach(this::registerEventHandler);
        }
    }

    @Override
    default void registerCapabilities() {
        for(Module module : Module.getActiveModules()) {
            module.getCapabilities().forEach(this::registerCapability);
        }
    }

    @Override
    default void activateRequiredModules() {}
}
