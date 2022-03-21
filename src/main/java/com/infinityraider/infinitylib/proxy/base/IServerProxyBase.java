package com.infinityraider.infinitylib.proxy.base;

import com.infinityraider.infinitylib.config.ConfigurationHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;

public interface IServerProxyBase<C extends ConfigurationHandler.SidedModConfig> extends IProxyBase<C> {
    @Override
    default Player getClientPlayer() {
        return null;
    }

    @Override
    default Level getClientWorld() {
        return null;
    }

    @Override
    default LogicalSide getLogicalSide() {
        // Can never be client
        return LogicalSide.SERVER;
    }

    @Override
    default Level getWorldFromDimension(ResourceKey<Level> dimension) {
        return this.getMinecraftServer().getLevel(dimension);
    }
}
