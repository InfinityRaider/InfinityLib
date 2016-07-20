package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.handler.ConfigurationHandler;
import com.infinityraider.infinitylib.InfinityModRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.*;

@SuppressWarnings("unused")
public abstract class CommonProxy implements IProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        this.initConfiguration(event);
        this.registerEventHandlers();
        InfinityModRegistry.getInstance().preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        InfinityModRegistry.getInstance().init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        InfinityModRegistry.getInstance().postInit(event);}

    @Override
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {}

    @Override
    public void onServerStarting(FMLServerStartingEvent event) {}

    @Override
    public void onServerStarted(FMLServerStartedEvent event) {}

    @Override
    public void onServerStopping(FMLServerStoppingEvent event) {}

    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {}

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        ConfigurationHandler.getInstance().init(event);
    }

    @Override
    public Entity getEntityById(int dimension, int id) {
        return getEntityById(getWorldByDimensionId(dimension), id);
    }

    @Override
    public Entity getEntityById(World world, int id) {
        return world.getEntityByID(id);
    }

    @Override
    public void registerEventHandlers() {
    }
}
