package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public interface IProxy extends IProxyBase {

    @Override
    default void registerEventHandlers() {
        Module.getActiveModules().forEach(module -> {
            module.getCommonEventHandlers().forEach(this::registerEventHandler);
        });
    }

    @Override
    default void registerCapabilities() {
        Module.getActiveModules().forEach(module -> {
            module.getCapabilities().forEach(this::registerCapability);
        });
    }

    @Override
    default void activateRequiredModules() {}

    @Override
    default void onCommonSetupEvent(FMLCommonSetupEvent event) {
        Module.getActiveModules().forEach(Module::init);
    }
}
