package com.infinityraider.infinitylib.proxy.base;

import com.infinityraider.infinitylib.config.ConfigurationHandler;
import com.infinityraider.infinitylib.modules.dynamiccamera.IDynamicCameraController;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.infinityraider.infinitylib.render.item.InfItemRendererRegistry;
import com.infinityraider.infinitylib.sound.SidedSoundDelegate;
import com.infinityraider.infinitylib.sound.SoundDelegateClient;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public interface IClientProxyBase<C extends ConfigurationHandler.SidedModConfig> extends IProxyBase<C> {
    @Override
    default Entity getRenderViewEntity() {
        return Minecraft.getInstance().getRenderViewEntity();
    }

    default void setRenderViewEntity(Entity entity) {
        Minecraft.getInstance().setRenderViewEntity(entity);
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

    /**
     * @return the fov setting on the client, 90 on the server
     */
    @Override
    default double getFieldOfView() {
        return Minecraft.getInstance().gameSettings.fov;
    }

    @Override
    default Item.Properties setItemRenderer(Item.Properties properties) {
        return properties.setISTER(InfItemRendererRegistry.getInstance().getISTER());
    }

    @Override
    default boolean toggleDynamicCamera(IDynamicCameraController controller, boolean status) {
            return ModuleDynamicCamera.getInstance().toggleObserving(controller, status);
    }
}
