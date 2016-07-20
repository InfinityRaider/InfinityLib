package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.handler.ConfigurationHandler;
import com.infinityraider.infinitylib.InfinityModRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy implements IClientProxyBase {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        InfinityModRegistry.getInstance().initRenderers();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        super.initConfiguration(event);
        ConfigurationHandler.getInstance().initClientConfigs(event);
    }

    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();
    }
}
