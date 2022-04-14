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
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
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
    public void registerRegistrables(InfinityMod<?,?> mod) {
        // Forward to common proxy
        IProxy.super.registerRegistrables(mod);
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

    private void registerBlockRenderers(ModContentRegistry blockRegistry) {
        if (blockRegistry == null) {
            return;
        }
        blockRegistry.stream(RegistryInitializer.Type.BLOCK)
                .map(RegistryInitializer::get)
                .filter(obj -> obj instanceof IInfinityBlock)
                .map(obj -> (IInfinityBlock) obj)
                .forEach(block -> {
                    // Set render type
                    ItemBlockRenderTypes.setRenderLayer(block.cast(), block.getRenderType());
                    // Register block color
                    BlockColor color = block.getColor();
                    if (color != null) {
                        Minecraft.getInstance().getBlockColors().register(color, block.cast());
                    }
                });
    }

    private void registerItemRenderers(ModContentRegistry itemRegistry) {
        if (itemRegistry == null) {
            return;
        }
        itemRegistry.stream(RegistryInitializer.Type.ITEM)
                .map(RegistryInitializer::get)
                .filter(obj -> obj instanceof IInfinityItem)
                .map(obj -> (IInfinityItem) obj)
                .forEach(item -> InfItemRendererRegistry.getInstance().register(item));
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
