package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.proxy.base.IServerProxyBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.*;

@SuppressWarnings("unused")
@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy implements IProxy, IServerProxyBase {
    public ServerProxy() {}

    @Override
    public void onDedicatedServerSetupEvent(FMLDedicatedServerSetupEvent event) {
        Module.getActiveModules().forEach(Module::postInit);
    }
}
