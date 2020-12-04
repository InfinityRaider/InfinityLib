package com.infinityraider.infinitylib.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public abstract class Config implements ConfigurationHandler.SidedModConfig {

    private Config() {}

    public abstract boolean debug();

    public static class Common extends Config {
        public final ForgeConfigSpec.BooleanValue debug;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Debug");
            this.debug = builder.comment("Set to true if you wish to enable debug mode.")
                    .define("debug", false);
            builder.pop();
        }

        @Override
        public boolean debug() {
            return this.debug.get();
        }

        @Override
        public ModConfig.Type getSide() {
            return ModConfig.Type.COMMON;
        }
    }

}
