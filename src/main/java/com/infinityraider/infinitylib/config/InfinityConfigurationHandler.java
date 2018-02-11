package com.infinityraider.infinitylib.config;

import com.infinityraider.infinitylib.InfinityMod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.HashMap;
import java.util.Map;

public class InfinityConfigurationHandler {
    private final InfinityMod mod;

    private Configuration config;
    private Map<String, ConfigEntry> entries;

    public InfinityConfigurationHandler(InfinityMod mod) {
        this.mod = mod;
        this.entries = new HashMap<>();
    }

    public InfinityMod getMod() {
        return this.mod;
    }

    public Configuration getConfiguration() {
        return this.config;
    }

    void addEntry(ConfigEntry entry) {
        this.entries.put(entry.getName(), entry);
    }

    public InfinityConfigurationHandler updateEntries() {
        this.entries.values().forEach(ConfigEntry::initialize);
        if(this.config.hasChanged()) {
            this.config.save();
        }
        return this;
    }

    public InfinityConfigurationHandler initializeConfiguration(FMLPreInitializationEvent event) {
        if(this.config == null) {
            this.config = new Configuration(event.getSuggestedConfigurationFile());
        }
        this.getMod().getConfiguration().initializeConfiguration(this);
        return this.updateEntries();
    }

    public InfinityConfigurationHandler initializeConfigurationClient(FMLPreInitializationEvent event) {
        if(this.config == null) {
            this.config = new Configuration(event.getSuggestedConfigurationFile());
        }
        this.getMod().getConfiguration().initializeConfigurationClient(this);
        return this.updateEntries();
    }
}