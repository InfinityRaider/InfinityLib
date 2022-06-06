package com.infinityraider.infinitylib.capability;

import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class CapabilityHandler {
    private static final CapabilityHandler INSTANCE = new CapabilityHandler();

    public static CapabilityHandler getInstance() {
        return INSTANCE;
    }

    private final Set<ICapabilityImplementation<ICapabilityProvider, ?>> capabilityImplementations;

    private CapabilityHandler() {
        this.capabilityImplementations = Sets.newConcurrentHashSet();
        InfinityLib.instance.registerEventHandler(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends ICapabilityProvider, C> void registerCapability(ICapabilityImplementation<T, C> implementation) {
        this.capabilityImplementations.add((ICapabilityImplementation<ICapabilityProvider, ?>) implementation);
    }

    @SubscribeEvent
    @SuppressWarnings({"unchecked","unused"})
    // We must not define a generic type, or this method will not pass the AMSEventHandler generics filter test
    public void addCapabilitiesRaw(AttachCapabilitiesEvent event) {
        if(!(event.getObject() instanceof ICapabilityProvider)) {
            return;
        }
        this.addCapabilitiesParametric(event);
    }

    @SuppressWarnings("unchecked")
    private <T extends ICapabilityProvider> void addCapabilitiesParametric(AttachCapabilitiesEvent<T> event) {
        T carrier = event.getObject();
        Class<T> clazz = (Class<T>) carrier.getClass();
        this.capabilityImplementations.stream()
                .filter(impl -> impl.getCarrierClass().isAssignableFrom(clazz))
                .filter(impl -> impl.shouldApplyCapability(carrier))
                .forEach(impl -> event.addCapability(impl.getCapabilityKey(), impl.createProvider(carrier)));
    }

    @SubscribeEvent
    @SuppressWarnings({"unchecked","unused"})
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        this.capabilityImplementations.stream()
                .filter(impl -> impl instanceof IInfCapabilityImplementation)
                .map(impl -> (IInfCapabilityImplementation<?,?>) impl)
                .forEach(impl -> event.register(impl.getCapabilityClass()));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getPlayer();
        if(oldPlayer != null && newPlayer != null && !oldPlayer.getLevel().isClientSide()) {
            this.capabilityImplementations.stream()
                    .filter(impl -> Player.class.isAssignableFrom(impl.getCarrierClass()))
                    .filter(impl -> impl instanceof IInfCapabilityImplementation)
                    .map(impl -> (IInfCapabilityImplementation<?, ?>) impl)
                    .forEach(impl ->
                            oldPlayer.getCapability(impl.getCapability(), null).ifPresent(oldProps ->
                                    newPlayer.getCapability(impl.getCapability(), null).ifPresent(newProps ->
                                            impl.copyData(oldProps, newProps))));
        }
    }
}
