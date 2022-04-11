package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.block.IInfinityBlock;
import com.infinityraider.infinitylib.config.Config;
import com.infinityraider.infinitylib.container.IInfinityContainerMenuType;
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
import com.infinityraider.infinitylib.utility.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
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
    public void registerRegistrables(InfinityMod<?,?> mod, IEventBus bus) {
        // Forward to common proxy
        IProxy.super.registerRegistrables(mod, bus);
        // Register client side stuff
        mod.getModModelLoaders().forEach(loader -> ModelLoaderRegistrar.getInstance().registerModelLoader(loader));
        RenderRegisteringHandler.getInstance().registerTileRegistry(mod.getModTileRegistry());
        RenderRegisteringHandler.getInstance().registerEntityRegistry(mod.getModEntityRegistry());
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
    public void registerModBusEventHandlers(IEventBus bus) {
        IProxy.super.registerModBusEventHandlers(bus);
        bus.addListener(ModelLoaderRegistrar.getInstance()::registerModelLoaders);
        bus.addListener(ParticleHelper.getInstance()::onFactoryRegistration);
        bus.addListener(RenderRegisteringHandler.getInstance()::registerRenderers);
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
    public void registerGuiContainer(IInfinityContainerMenuType containerType) {
        IInfinityContainerMenuType.IGuiFactory<?> factory = containerType.getGuiFactory();
        if(factory != null) {
            MenuScreens.register(containerType.cast(), IInfinityContainerMenuType.castGuiFactory(factory));
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
        this.registerItemRenderers(mod.getModItemRegistry());
    }

    private void registerBlockRenderers(Class<?> blockRegistry) {
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

    private void registerItemRenderers(Class<?> itemRegistry) {
        if (itemRegistry == null) {
            return;
        }
        ReflectionHelper.forEachValueIn(itemRegistry, IInfinityItem.class, object -> InfItemRendererRegistry.getInstance().register(object));
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
