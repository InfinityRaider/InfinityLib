package com.infinityraider.infinitylib.proxy.base;

import com.infinityraider.infinitylib.config.ConfigurationHandler;
import com.infinityraider.infinitylib.modules.dynamiccamera.IDynamicCameraController;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.infinityraider.infinitylib.sound.SidedSoundDelegate;
import com.infinityraider.infinitylib.sound.SoundDelegateClient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;

public interface IClientProxyBase<C extends ConfigurationHandler.SidedModConfig> extends IProxyBase<C> {
    @Override
    default Entity getRenderViewEntity() {
        return Minecraft.getInstance().getCameraEntity();
    }

    default void setRenderViewEntity(Entity entity) {
        Minecraft.getInstance().setCameraEntity(entity);
    }

    @Override
    default Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    default Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    default Level getWorldFromDimension(ResourceKey<Level> dimension) {
        LogicalSide effectiveSide = this.getLogicalSide();
        if(effectiveSide == LogicalSide.SERVER) {
            return getMinecraftServer().getLevel(dimension);
        } else {
            return getClientWorld();
        }
    }

    @Override
    default void queueTask(Runnable task) {
        if(getLogicalSide() == LogicalSide.CLIENT) {
            Minecraft.getInstance().submit(task);
        } else {
            IProxyBase.super.queueTask(task);
        }
    }

    @Override
    default SidedSoundDelegate getSoundDelegate() {
        return new SoundDelegateClient(Minecraft.getInstance().getSoundManager());
    }

    /**
     * @return the fov setting on the client, 90 on the server
     */
    @Override
    default double getFieldOfView() {
        return Minecraft.getInstance().options.fov;
    }

    @Override
    default boolean toggleDynamicCamera(IDynamicCameraController controller, boolean status) {
            return ModuleDynamicCamera.getInstance().toggleObserving(controller, status);
    }
}
