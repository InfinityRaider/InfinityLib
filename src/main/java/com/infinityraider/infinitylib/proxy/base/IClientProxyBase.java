package com.infinityraider.infinitylib.proxy.base;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.config.ConfigurationHandler;
import com.infinityraider.infinitylib.entity.EmptyEntityRenderFactory;
import com.infinityraider.infinitylib.entity.IInfinityEntityType;
import com.infinityraider.infinitylib.sound.SidedSoundDelegate;
import com.infinityraider.infinitylib.sound.SoundDelegateClient;
import com.infinityraider.infinitylib.utility.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public interface IClientProxyBase<C extends ConfigurationHandler.SidedModConfig> extends IProxyBase<C> {

    @Override
    /** Called on the client to register renderers */
    default void registerRenderers(InfinityMod<?,?> mod) {
        this.registerEntityRenderers(mod.getModEntityRegistry());
    }

    default void registerEntityRenderers(Object entityRegistry) {
        if(entityRegistry != null) {
            ReflectionHelper.forEachValueIn(entityRegistry, IInfinityEntityType.class, object -> {
                if (object.getRenderFactory() == null) {
                    InfinityLib.instance.getLogger().info("", "No entity rendering factory was found for entity " + object.getInternalName());
                    RenderingRegistry.registerEntityRenderingHandler(object.cast(), EmptyEntityRenderFactory.getInstance());
                } else {
                    RenderingRegistry.registerEntityRenderingHandler(object.cast(), object.getRenderFactory());
                }
            });
        }
    }

    @Override
    default PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    default World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    default World getWorldFromDimension(RegistryKey<World> dimension) {
        LogicalSide effectiveSide = this.getLogicalSide();
        if(effectiveSide == LogicalSide.SERVER) {
            return ServerLifecycleHooks.getCurrentServer().getWorld(dimension);
        } else {
            return getClientWorld();
        }
    }

    @Override
    default void queueTask(Runnable task) {
        if(getLogicalSide() == LogicalSide.CLIENT) {
            Minecraft.getInstance().execute(task);
        } else {
            this.getMinecraftServer().execute(task);
        }
    }

    @Override
    default SidedSoundDelegate getSoundDelegate() {
        return new SoundDelegateClient(Minecraft.getInstance().getSoundHandler());
    }
}
