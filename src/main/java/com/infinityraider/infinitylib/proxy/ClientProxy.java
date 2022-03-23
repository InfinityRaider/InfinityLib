package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.block.IInfinityBlock;
import com.infinityraider.infinitylib.block.tile.IInfinityTileEntityType;
import com.infinityraider.infinitylib.config.Config;
import com.infinityraider.infinitylib.container.IInfinityContainerType;
import com.infinityraider.infinitylib.entity.EmptyEntityRenderSupplier;
import com.infinityraider.infinitylib.entity.IInfinityEntityType;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.particle.IInfinityParticleType;
import com.infinityraider.infinitylib.particle.ParticleHelper;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import com.infinityraider.infinitylib.render.RenderRegisteringHandler;
import com.infinityraider.infinitylib.render.fluid.InfFluidRenderer;
import com.infinityraider.infinitylib.render.item.InfItemRendererRegistry;
import com.infinityraider.infinitylib.render.model.TransformingFaceBakery;
import com.infinityraider.infinitylib.render.model.ModelLoaderRegistrar;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.infinityraider.infinitylib.render.tile.TileEntityRendererWrapper;
import com.infinityraider.infinitylib.utility.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.*;

import java.util.function.Consumer;

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
        InfFluidRenderer.init();
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
        bus.addListener(ParticleHelper.getInstance()::onFactoryRegistration);
    }

    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        this.registerEventHandler(RenderRegisteringHandler.getInstance());
        this.registerEventHandler(ModelLoaderRegistrar.getInstance());
        for (Module module : Module.getActiveModules()) {
            module.getClientEventHandlers().forEach(this::registerEventHandler);
        }
    }

    @Override
    public void registerGuiContainer(IInfinityContainerType containerType) {
        IInfinityContainerType.IGuiFactory<?> factory = containerType.getGuiFactory();
        if(factory != null) {
            MenuScreens.register(containerType.cast(), IInfinityContainerType.castGuiFactory(factory));
        }
    }

    @Override
    public <T extends ParticleOptions> void onParticleRegistration(IInfinityParticleType<T> particleType) {
        ParticleHelper.getInstance().registerType(particleType);
    }

    /** Called on the client to register renderers */
    @Override
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
            ItemBlockRenderTypes.setRenderLayer(object.cast(), object.getRenderType());
            // Register block color
            BlockColor color = object.getColor();
            if(color != null) {
                Minecraft.getInstance().getBlockColors().register(color, object.cast());
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void registerTileRenderers(Object tileRegistry) {
        if (tileRegistry == null) {
            return;
        }
        ReflectionHelper.forEachValueIn(tileRegistry, IInfinityTileEntityType.class, object -> {
            // Create Renderer
            ITileRenderer<? extends BlockEntity> renderer = object.getRenderer();
            if(renderer != null) {
                // Register TileEntityRendererWrapper
                RenderRegisteringHandler.getInstance().register(object.cast(), (dispatcher) -> TileEntityRendererWrapper.createWrapper(renderer));}
        });
    }

    private void registerItemRenderers(Object itemRegistry) {
        if (itemRegistry == null) {
            return;
        }
        ReflectionHelper.forEachValueIn(itemRegistry, IInfinityItem.class, object -> InfItemRendererRegistry.getInstance().register(object));
    }

    private void registerEntityRenderers(Object entityRegistry) {
        if (entityRegistry == null) {
            return;
        }
        ReflectionHelper.forEachValueIn(entityRegistry, IInfinityEntityType.class, object -> {
            if (object.getRenderSupplier() == null) {
                InfinityLib.instance.getLogger().info("", "No entity rendering factory was found for entity " + object.getInternalName());
                RenderRegisteringHandler.getInstance().register(object.cast(), EmptyEntityRenderSupplier.getInstance());
            } else {
                RenderRegisteringHandler.getInstance().register(object.cast(), object.getRenderSupplier());
            }
        });
    }

    @Override
    public void forceClientRenderUpdate(BlockPos pos) {
        Minecraft.getInstance().levelRenderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void initItemRenderer(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(InfItemRendererRegistry.getInstance().getItemRenderer());
    }
}
