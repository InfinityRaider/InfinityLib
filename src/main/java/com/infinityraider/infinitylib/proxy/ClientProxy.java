package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.handler.ConfigurationHandler;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import com.infinityraider.infinitylib.utility.ModHelper;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy, IClientProxyBase {
    @Override
    public void preInitStart(FMLPreInitializationEvent event) {
        IProxy.super.preInitStart(event);
        ConfigurationHandler.getInstance().initClientConfigs(event);
    }

    @Override
    public void registerRenderers(InfinityMod mod) {
        ModHelper.getInstance().initRenderers(mod);
    }
}
