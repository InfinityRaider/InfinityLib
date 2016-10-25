package com.infinityraider.infinitylib.handler;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigurationHandler {
    private static final ConfigurationHandler INSTANCE = new ConfigurationHandler();

    private ConfigurationHandler() {}

    public static ConfigurationHandler getInstance() {
        return INSTANCE;
    }

    private Configuration config;

    //debug
    public boolean debug;

    public void init(FMLPreInitializationEvent event) {
        if(config == null) {
            config = new Configuration(event.getSuggestedConfigurationFile());
        }
        loadConfiguration();
        if(config.hasChanged()) {
            config.save();
        }
        InfinityLib.instance.getLogger().debug("Configuration Loaded");
    }

    @SideOnly(Side.CLIENT)
    public void initClientConfigs(FMLPreInitializationEvent event) {
        if(config == null) {
            config = new Configuration(event.getSuggestedConfigurationFile());
        }
        loadClientConfiguration(event);
        if(config.hasChanged()) {
            config.save();
        }
        InfinityLib.instance.getLogger().debug("Client configuration Loaded");
    }

    private void loadConfiguration() {
        //debug
        debug = config.getBoolean("debug", Categories.DEBUG.getName(), false, "Set to true if you wish to enable debug mode");
    }

    @SideOnly(Side.CLIENT)
    private void loadClientConfiguration(FMLPreInitializationEvent event) {

    }

    public enum Categories {
        DEBUG("debug");

        private final String name;

        Categories(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
