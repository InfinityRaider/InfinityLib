package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.*;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy, IClientProxyBase {
    public ClientProxy() {}

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        IProxy.super.onCommonSetupEvent(event);
        Module.getActiveModules().forEach(Module::initClient);
    }

    @Override
    public void onClientSetupEvent(final FMLClientSetupEvent event) {
        Module.getActiveModules().forEach(Module::postInit);
        Module.getActiveModules().forEach(Module::postInitClient);
    }

    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        for (Module module : Module.getActiveModules()) {
            module.getClientEventHandlers().forEach(this::registerEventHandler);
        }
    }
}
