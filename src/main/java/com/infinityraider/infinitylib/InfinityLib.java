package com.infinityraider.infinitylib;

import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.network.MessageSetEntityDead;
import com.infinityraider.infinitylib.network.MessageSyncTile;
import com.infinityraider.infinitylib.proxy.ClientProxy;
import com.infinityraider.infinitylib.proxy.IProxy;
import com.infinityraider.infinitylib.proxy.ServerProxy;
import com.infinityraider.infinitylib.reference.Reference;
import com.infinityraider.infinitylib.sound.MessagePlaySound;
import com.infinityraider.infinitylib.sound.MessageStopSound;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Reference.MOD_ID)
public class InfinityLib extends InfinityMod<IProxy> {
    public static InfinityLib instance;

    @Override
    public String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    protected void onModConstructed() {
        instance = this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected IProxy createClientProxy() {
        return new ClientProxy();
    }

    @Override
    @OnlyIn(Dist.DEDICATED_SERVER)
    protected IProxy createServerProxy() {
        return new ServerProxy();
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageSetEntityDead.class);
        wrapper.registerMessage(MessageSyncTile.class);
        wrapper.registerMessage(MessagePlaySound.class);
        wrapper.registerMessage(MessageStopSound.class);
        Module.getActiveModules().stream().sorted().forEach(m -> m.registerMessages(wrapper));
    }

    @SubscribeEvent //ModBus, can't use addListener due to nested genetics.
    public void registerRecipeSerialziers(RegistryEvent.Register<IRecipeSerializer<?>> event) {

    }
}