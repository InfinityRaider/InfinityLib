package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.handler.ConfigurationHandler;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy extends IProxyBase {
    @Override
    default void preInitStart(FMLPreInitializationEvent event) {
        ConfigurationHandler.getInstance().init(event);
    }

    void registerRenderers(InfinityMod mod);
}
