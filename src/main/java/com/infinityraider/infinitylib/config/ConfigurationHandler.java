package com.infinityraider.infinitylib.config;

import com.infinityraider.infinitylib.InfinityMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.function.Function;

public class ConfigurationHandler<T extends ConfigurationHandler.SidedModConfig> {
    private final T config;
    private final ForgeConfigSpec spec;

    public ConfigurationHandler(ModLoadingContext context, Function<ForgeConfigSpec.Builder, T> constructor, InfinityMod<?, T> mod) {
        File modConfigDir = new File(FMLPaths.CONFIGDIR.get().toFile(), mod.getModId());
        if(modConfigDir.exists() || modConfigDir.mkdirs()) {
            Pair<T, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(constructor);
            this.config = pair.getLeft();
            this.spec = pair.getRight();
            context.registerConfig(
                    this.getConfig().getSide(),
                    this.getSpec(),
                    mod.getModId() + "/config-" + this.getConfig().getSide().name().toLowerCase() +".toml"
            );
        } else {
            throw new IllegalStateException("Could not evaluate config dir for " + mod.getModId());
        }
    }

    public T getConfig() {
        return this.config;
    }

    public ForgeConfigSpec getSpec() {
        return this.spec;
    }

    public interface SidedModConfig {
        ModConfig.Type getSide();
    }
}
