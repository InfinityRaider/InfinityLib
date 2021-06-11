package com.infinityraider.infinitylib.proxy.base;

import com.infinityraider.infinitylib.config.ConfigurationHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public interface IServerProxyBase<C extends ConfigurationHandler.SidedModConfig> extends IProxyBase<C> {
    @Override
    default PlayerEntity getClientPlayer() {
        return null;
    }

    @Override
    default World getClientWorld() {
        return null;
    }

    @Override
    default LogicalSide getLogicalSide() {
        // Can never be client
        return LogicalSide.SERVER;
    }

    @Override
    default World getWorldFromDimension(RegistryKey<World> dimension) {
        return ServerLifecycleHooks.getCurrentServer().getWorld(dimension);
    }
}
