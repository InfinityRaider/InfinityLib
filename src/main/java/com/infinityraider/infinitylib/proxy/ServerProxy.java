package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.config.Config;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.proxy.base.IServerProxyBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.*;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy implements IProxy, IServerProxyBase<Config>  {
    public ServerProxy() {}

    @Override
    public void onDedicatedServerSetupEvent(FMLDedicatedServerSetupEvent event) {
        Module.getActiveModules().forEach(Module::postInit);
    }
}
