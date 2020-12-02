package com.infinityraider.infinitylib.capability;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.utility.ISerializable;
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

    private final Map<Class<? extends ICapabilityProvider>, List<ICapabilityImplementation<ICapabilityProvider, ? extends ISerializable>>> map;

    private CapabilityHandler() {
        this.map = new HashMap<>();
        InfinityLib.instance.registerEventHandler(this);
    }

    @SuppressWarnings("unchecked")
    public void registerCapability(ICapabilityImplementation implementation) {
        if(!this.map.containsKey(implementation.getCarrierClass())) {
            this.map.put(implementation.getCarrierClass(), new ArrayList<>());
        }
        this.map.get(implementation.getCarrierClass()).add(implementation);
        CapabilityManager.INSTANCE.register(implementation.getCapabilityClass(), new CapabilityStorage(), () -> null);
    }

    protected <T extends ICapabilityProvider, C extends ISerializable> void addCapability(
            AttachCapabilitiesEvent event, ICapabilityImplementation<T , C> implementation, T carrier) {
        C value = implementation.createNewValue(carrier);
        event.addCapability(implementation.getCapabilityKey(), new CapabilityProvider<>(implementation.getCapability(), value));
    }

    @SuppressWarnings("unchecked")
    protected  <T extends ICapabilityProvider> void addCapabilities(AttachCapabilitiesEvent<T> event) {
        Object object = event.getObject();
        T carrier = (T) object;
        Class<T> clazz = (Class<T>) carrier.getClass();
        this.map.entrySet().stream().filter(entry -> entry.getKey().isAssignableFrom(clazz)).forEach(
                entry -> entry.getValue().stream()
                        .filter(impl -> impl.getCarrierClass().isAssignableFrom(clazz))
                        .filter(impl -> impl.shouldApplyCapability(carrier))
                        .forEach(impl -> this.addCapability(event, impl, carrier)));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addEntityCapabilitiesRaw(AttachCapabilitiesEvent event) {
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
            List<ICapabilityImplementation<ICapabilityProvider, ? extends ISerializable>> list = this.map.get(oldPlayer.getClass());
            if(list == null) {
                return;
            }
            list.stream()
                    .filter(impl -> PlayerEntity.class.isAssignableFrom(impl.getCarrierClass()))
                    .forEach(impl -> {
                        oldPlayer.getCapability(impl.getCapability(), null).ifPresent(oldProps -> {
                            newPlayer.getCapability(impl.getCapability(), null).ifPresent(newProps -> {
                                newProps.readFromNBT(oldProps.writeToNBT());
                            });
                        });
                    });
        }
    }
}
