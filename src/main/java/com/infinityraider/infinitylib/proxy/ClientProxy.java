package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.config.Config;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import com.infinityraider.infinitylib.render.model.TransformingFaceBakery;
import com.infinityraider.infinitylib.render.model.ModelLoaderRegistrar;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.*;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy, IClientProxyBase<Config> {
    public ClientProxy() {}

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        IProxy.super.onCommonSetupEvent(event);
        Module.getActiveModules().forEach(Module::initClient);
        TransformingFaceBakery.getInstance().init();
    }

    @Override
    public void onClientSetupEvent(final FMLClientSetupEvent event) {
        Module.getActiveModules().forEach(Module::postInit);
        Module.getActiveModules().forEach(Module::postInitClient);
    }

    @Override
    public void registerFMLEventHandlers(IEventBus bus) {
        IProxy.super.registerFMLEventHandlers(bus);
        bus.addListener(ModelLoaderRegistrar.getInstance()::registerModelLoaders);
    }

    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        this.registerEventHandler(ModelLoaderRegistrar.getInstance());
        for (Module module : Module.getActiveModules()) {
            module.getClientEventHandlers().forEach(this::registerEventHandler);
        }
    }
}
