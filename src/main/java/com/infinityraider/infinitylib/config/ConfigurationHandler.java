package com.infinityraider.infinitylib.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

public class ConfigurationHandler<T extends ConfigurationHandler.SidedModConfig> {
    private final T config;
    private final ForgeConfigSpec spec;

    public ConfigurationHandler(ModLoadingContext context, Function<ForgeConfigSpec.Builder, T> constructor) {
        Pair<T, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(constructor);
        this.config = pair.getLeft();
        this.spec = pair.getRight();
        context.registerConfig(this.getConfig().getSide(), this.getSpec());
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
