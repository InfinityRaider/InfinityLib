package com.infinityraider.infinitylib.proxy.base;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.block.IInfinityBlock;
import com.infinityraider.infinitylib.config.ConfigurationHandler;
import com.infinityraider.infinitylib.entity.EmptyEntityRenderFactory;
import com.infinityraider.infinitylib.entity.IInfinityEntityType;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.item.property.IInfinityItemWithProperties;
import com.infinityraider.infinitylib.sound.SidedSoundDelegate;
import com.infinityraider.infinitylib.sound.SoundDelegateClient;
import com.infinityraider.infinitylib.utility.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public interface IClientProxyBase<C extends ConfigurationHandler.SidedModConfig> extends IProxyBase<C> {

    @Override
    /** Called on the client to register renderers */
    default void registerRenderers(InfinityMod<?,?> mod) {
        this.registerBlockRenderers(mod.getModBlockRegistry());
        this.registerItemRenderers(mod.getModItemRegistry());
        this.registerEntityRenderers(mod.getModEntityRegistry());
    }

    default void registerBlockRenderers(Object blockRegistry) {
        if (blockRegistry == null) {
            return;
        }
        ReflectionHelper.forEachValueIn(blockRegistry, IInfinityBlock.class, object -> {
            // Set render type
            RenderTypeLookup.setRenderLayer(object.cast(), object.getRenderType());
        });
    }

    default void registerItemRenderers(Object itemRegistry) {
        if (itemRegistry == null) {
            return;
        }
        ReflectionHelper.forEachValueIn(itemRegistry, IInfinityItem.class, object -> {
            if(object instanceof IInfinityItemWithProperties) {
                ((IInfinityItemWithProperties) object).getProperties().forEach(prop ->{
                    ItemModelsProperties.registerProperty(object.cast(), prop.getId(), prop::getValue);
                });
            }
        });
    }

    default void registerEntityRenderers(Object entityRegistry) {
        if (entityRegistry == null) {
            return;
        }
        ReflectionHelper.forEachValueIn(entityRegistry, IInfinityEntityType.class, object -> {
            if (object.getRenderFactory() == null) {
                InfinityLib.instance.getLogger().info("", "No entity rendering factory was found for entity " + object.getInternalName());
                RenderingRegistry.registerEntityRenderingHandler(object.cast(), EmptyEntityRenderFactory.getInstance());
            } else {
                RenderingRegistry.registerEntityRenderingHandler(object.cast(), object.getRenderFactory());
            }
        });
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
