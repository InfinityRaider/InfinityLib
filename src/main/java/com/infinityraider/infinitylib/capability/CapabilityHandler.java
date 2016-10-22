package com.infinityraider.infinitylib.capability;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class CapabilityHandler {
    private static final CapabilityHandler INSTANCE = new CapabilityHandler();

    public static CapabilityHandler getInstance() {
        return INSTANCE;
    }

    private final List<ICapabilityImplementation<Entity, ? extends ISerializable>> entityCapabilities;
    private final List<ICapabilityImplementation<TileEntity, ? extends ISerializable>> tileEntityCapabilities;
    private final List<ICapabilityImplementation<ItemStack, ? extends ISerializable>> itemCapabilities;
    private final List<ICapabilityImplementation<World, ? extends ISerializable>> worldCapabilities;

    private CapabilityHandler() {
        this.entityCapabilities = new ArrayList<>();
        this.tileEntityCapabilities = new ArrayList<>();
        this.itemCapabilities = new ArrayList<>();
        this.worldCapabilities = new ArrayList<>();
        InfinityLib.proxy.registerEventHandler(this);
    }

    @SuppressWarnings("unchecked")
    public boolean registerCapability(ICapabilityImplementation implementation) {
        if(this.registerCapabilityForType(implementation)) {
            CapabilityManager.INSTANCE.register(implementation.getCapabilityClass(), new CapabilityStorage(), implementation);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected boolean registerCapabilityForType(ICapabilityImplementation implementation) {
        Class<?> clazz = implementation.getCarrierClass();
        if(Entity.class.isAssignableFrom(clazz)) {
            entityCapabilities.add((ICapabilityImplementation<Entity, ?>) implementation);
            return true;
        }
        if(TileEntity.class.isAssignableFrom(clazz)) {
            tileEntityCapabilities.add((ICapabilityImplementation<TileEntity, ?>) implementation);
            return true;
        }
        if(Item.class.isAssignableFrom(clazz)) {
            itemCapabilities.add((ICapabilityImplementation<ItemStack, ?>) implementation);
            return true;
        }
        if(World.class.isAssignableFrom(clazz)) {
            worldCapabilities.add((ICapabilityImplementation<World, ?>) implementation);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addEntityCapabilities(AttachCapabilitiesEvent.Entity event) {
        entityCapabilities.stream()
                .filter(impl -> impl.getCarrierClass().isAssignableFrom(event.getEntity().getClass()))
                .filter(impl -> impl.shouldApplyCapability(event.getEntity()))
                .forEach(impl -> this.addCapability(event, impl, event.getEntity()));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addTileEntityCapabilities(AttachCapabilitiesEvent.TileEntity event) {
        tileEntityCapabilities.stream()
                .filter(impl -> impl.getCarrierClass().isAssignableFrom(event.getTileEntity().getClass()))
                .filter(impl -> impl.shouldApplyCapability(event.getTileEntity()))
                .forEach(impl -> this.addCapability(event, impl, event.getTileEntity()));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addItemCapabilities(AttachCapabilitiesEvent.Item event) {
        itemCapabilities.stream()
                .filter(impl -> impl.shouldApplyCapability(event.getItemStack()))
                .forEach(impl -> this.addCapability(event, impl, event.getItemStack()));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addWorldCapabilities(AttachCapabilitiesEvent.World event) {
        worldCapabilities.stream()
                .filter(impl -> impl.getCarrierClass().isAssignableFrom(event.getWorld().getClass()))
                .filter(impl -> impl.shouldApplyCapability(event.getWorld()))
                .forEach(impl -> this.addCapability(event, impl, event.getWorld()));
    }

    protected  <T extends ICapabilityProvider, C extends ISerializable> void addCapability(
            AttachCapabilitiesEvent event, ICapabilityImplementation<T , C> implementation, T carrier) {

        C value = implementation.onValueAddedToCarrier(implementation.getCapability().getDefaultInstance(), carrier);
        event.addCapability(implementation.getCapabilityKey(), new CapabilityProvider<>(implementation.getCapability(), value));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer oldPlayer = event.getOriginal();
        EntityPlayer newPlayer = event.getEntityPlayer();
        if(oldPlayer != null && newPlayer != null && !oldPlayer.getEntityWorld().isRemote) {
            entityCapabilities.stream()
                    .filter(impl -> EntityPlayer.class.isAssignableFrom(impl.getCarrierClass()))
                    .filter(impl -> oldPlayer.hasCapability(impl.getCapability(), null))
                    .forEach(impl -> {
                        ISerializable oldProps = oldPlayer.getCapability(impl.getCapability(), null);
                        ISerializable newProps = oldPlayer.getCapability(impl.getCapability(), null);
                        if(newProps != null && oldProps != null) {
                            newProps.readFromNBT(oldProps.writeToNBT());
                        }
                    });
        }
    }
}
