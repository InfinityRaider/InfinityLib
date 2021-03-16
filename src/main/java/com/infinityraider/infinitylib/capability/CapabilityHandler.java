package com.infinityraider.infinitylib.capability;

import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
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
    public void registerCapability(ICapabilityImplementation<ICapabilityProvider, ?> implementation) {
        this.capabilityImplementations.add(implementation);
        if(implementation instanceof IInfCapabilityImplementation) {
            CapabilityManager.INSTANCE.register(
                    ((IInfCapabilityImplementation<ICapabilityProvider, ?>) implementation).getCapabilityClass(),
                    new CapabilityStorage<>(), () -> null
            );
        }
    }

    protected <T extends ICapabilityProvider, C> void addCapability(
            AttachCapabilitiesEvent<?> event, ICapabilityImplementation<T , C> implementation, T carrier) {
        event.addCapability(
                implementation.getCapabilityKey(),
                new CapabilityProvider<>(implementation::getCapability, implementation.createNewValue(carrier))
        );
    }

    @SuppressWarnings("unchecked")
    protected  <T extends ICapabilityProvider> void addCapabilities(AttachCapabilitiesEvent<T> event) {
        T carrier = event.getObject();
        Class<T> clazz = (Class<T>) carrier.getClass();
        this.capabilityImplementations.stream()
                .filter(impl -> impl.getCarrierClass().isAssignableFrom(clazz))
                .filter(impl -> impl.shouldApplyCapability(carrier))
                .forEach(impl -> this.addCapability(event, impl, carrier));
    }

    @SubscribeEvent
    @SuppressWarnings({"unused", "unchecked"})
    public void addCapabilitiesRaw(AttachCapabilitiesEvent event) {
        if(event.getObject() instanceof ICapabilityProvider) {
            this.addCapabilities(event);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity oldPlayer = event.getOriginal();
        PlayerEntity newPlayer = event.getPlayer();
        if(oldPlayer != null && newPlayer != null && !oldPlayer.getEntityWorld().isRemote) {
            this.capabilityImplementations.stream()
                    .filter(impl -> PlayerEntity.class.isAssignableFrom(impl.getCarrierClass()))
                    .filter(impl -> impl instanceof IInfCapabilityImplementation)
                    .map(impl -> (IInfCapabilityImplementation<?, ?>) impl)
                    .forEach(impl ->
                            oldPlayer.getCapability(impl.getCapability(), null).ifPresent(oldProps ->
                                    newPlayer.getCapability(impl.getCapability(), null).ifPresent(newProps ->
                                            newProps.readFromNBT(oldProps.writeToNBT())
                                    )));
        }
    }
}
