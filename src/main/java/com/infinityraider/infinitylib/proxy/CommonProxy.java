package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.handler.ConfigurationHandler;
import net.minecraftforge.fml.common.event.*;

@SuppressWarnings("unused")
public abstract class CommonProxy implements IProxyBase {
    @Override
    public void registerEventHandlers() {}

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        ConfigurationHandler.getInstance().init(event);
    }
}
