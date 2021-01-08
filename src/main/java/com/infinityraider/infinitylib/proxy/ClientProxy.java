package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.block.IInfinityBlock;
import com.infinityraider.infinitylib.block.tile.IInfinityTileEntityType;
import com.infinityraider.infinitylib.config.Config;
import com.infinityraider.infinitylib.container.IInfinityContainerType;
import com.infinityraider.infinitylib.entity.EmptyEntityRenderFactory;
import com.infinityraider.infinitylib.entity.IInfinityEntityType;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import com.infinityraider.infinitylib.render.item.InfItemRendererRegistry;
import com.infinityraider.infinitylib.render.model.TransformingFaceBakery;
import com.infinityraider.infinitylib.render.model.ModelLoaderRegistrar;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.infinityraider.infinitylib.render.tile.TileEntityRendererWrapper;
import com.infinityraider.infinitylib.utility.ReflectionHelper;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.*;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy, IClientProxyBase<Config> {
    public ClientProxy() {}

    @Override
    public void registerRegistrables(InfinityMod<?,?> mod) {
        // Forward to common proxy
        IProxy.super.registerRegistrables(mod);
        // Register client side stuff
        mod.getModModelLoaders().forEach(loader -> ModelLoaderRegistrar.getInstance().registerModelLoader(loader));
    }

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        IProxy.super.onCommonSetupEvent(event);
        Module.getActiveModules().forEach(Module::initClient);
        TransformingFaceBakery.init();
    }

    @Override
    public void onClientSetupEvent(final FMLClientSetupEvent event) {
        Module.getActiveModules().forEach(Module::postInit);
        Module.getActiveModules().forEach(Module::postInitClient);
    }

    @Override
    public void registerFMLEventHandlers(IEventBus bus) {
        IProxy.super.registerFMLEventHandlers(bus);
        bus.addListener(ModelLoaderRegistrar.getInstance()::registerModelLoaders);
    }

    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        this.registerEventHandler(ModelLoaderRegistrar.getInstance());
        for (Module module : Module.getActiveModules()) {
            module.getClientEventHandlers().forEach(this::registerEventHandler);
        }
    }

    @Override
    public void registerGuiContainer(IInfinityContainerType containerType) {
        IInfinityContainerType.IGuiFactory<?> factory = containerType.getGuiFactory();
        if(factory != null) {
            ScreenManager.registerFactory(containerType.cast(), IInfinityContainerType.castGuiFactory(factory));
        }
    }

    @Override
    /** Called on the client to register renderers */
    public void registerRenderers(InfinityMod<?,?> mod) {
        this.registerBlockRenderers(mod.getModBlockRegistry());
        this.registerTileRenderers(mod.getModTileRegistry());
        this.registerItemRenderers(mod.getModItemRegistry());
        this.registerEntityRenderers(mod.getModEntityRegistry());
    }

    private void registerBlockRenderers(Object blockRegistry) {
        if (blockRegistry == null) {
            return;
        }
        ReflectionHelper.forEachValueIn(blockRegistry, IInfinityBlock.class, object -> {
            // Set render type
            RenderTypeLookup.setRenderLayer(object.cast(), object.getRenderType());
        });
    }

    @SuppressWarnings("unchecked")
    private void registerTileRenderers(Object tileRegistry) {
        if (tileRegistry == null) {
            return;
        }
        ReflectionHelper.forEachValueIn(tileRegistry, IInfinityTileEntityType.class, object -> {
            // Create Renderer
            ITileRenderer<? extends TileEntity> renderer = object.getRenderer();
            if(renderer != null) {
                // Register TileEntityRendererWrapper
                ClientRegistry.bindTileEntityRenderer(object.cast(), (dispatcher) -> TileEntityRendererWrapper.createWrapper(dispatcher, renderer));}
        });
    }

    private void registerItemRenderers(Object itemRegistry) {
        if (itemRegistry == null) {
            return;
        }
        ReflectionHelper.forEachValueIn(itemRegistry, IInfinityItem.class, object -> {
            // Register custom item renderers
            InfItemRendererRegistry.getInstance().register(object);
            // Register model properties
            object.getModelProperties().forEach(prop -> ItemModelsProperties.registerProperty(object.cast(), prop.getId(), prop::getValue));
        });
    }

    private void registerEntityRenderers(Object entityRegistry) {
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
}
