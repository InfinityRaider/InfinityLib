package com.infinityraider.infinitylib.proxy;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.capability.CapabilityHandler;
import com.infinityraider.infinitylib.config.Config;
import com.infinityraider.infinitylib.container.IInfinityContainerMenuType;
import com.infinityraider.infinitylib.crafting.IngredientSerializerRegistrar;
import com.infinityraider.infinitylib.entity.EntityHandler;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.particle.IInfinityParticleType;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import com.infinityraider.infinitylib.world.StructureRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IProxy extends IProxyBase<Config> {

    @Override
    default  Function<ForgeConfigSpec.Builder, Config> getConfigConstructor() {
        return Config.Common::new;
    }

    @Override
    default void registerModBusEventHandlers(IEventBus bus) {
        bus.addListener(CapabilityHandler.getInstance()::registerCapabilities);
    }

    @Override
    default void registerEventHandlers() {
        Module.getActiveModules().forEach(module -> module.getCommonEventHandlers().forEach(this::registerEventHandler));
        this.registerEventHandler(EntityHandler.getInstance());
    }

    @Override
    default void registerCapabilities() {
        Module.getActiveModules().forEach(module -> module.getCapabilities().forEach(this::registerCapability));
    }

    @Override
    default void activateRequiredModules() {}

    @Override
    default void onCommonSetupEvent(FMLCommonSetupEvent event) {
        Module.getActiveModules().forEach(Module::init);
        IngredientSerializerRegistrar.getInstance().registerSerializers();
        StructureRegistry.getInstance().injectStructures();
    }

    @Override
    default void onServerAboutToStartEvent(final ServerAboutToStartEvent event) {}

    default void forceClientRenderUpdate(BlockPos pos) {}

    default void initItemRenderer(Consumer<IItemRenderProperties> consumer) {}

    /**
     * -------------------
     * REGISTERING METHODS
     * -------------------
     */

    default void registerRegistrables(InfinityMod<?,?> mod) {
        this.registerStructures(mod);
    }

    default <T extends ParticleOptions> void onParticleRegistration(IInfinityParticleType<T> particleType) {}

    default void registerStructures(InfinityMod<?,?> mod) {
        mod.getStructureRegistry();
    }

    default void registerGuiContainer(IInfinityContainerMenuType containerType) {}

    default void registerRenderers(InfinityMod<?,?> mod) {}
}
