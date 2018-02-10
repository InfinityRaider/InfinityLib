package com.infinityraider.infinitylib;

import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.network.MessageSetEntityDead;
import com.infinityraider.infinitylib.network.MessageSyncTile;
import com.infinityraider.infinitylib.proxy.IProxy;
import com.infinityraider.infinitylib.reference.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class InfinityLib extends InfinityMod {

    @Mod.Instance(Reference.MOD_ID)
    public static InfinityLib instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Override
    public IProxy proxy() {
        return proxy;
    }

    @Override
    public String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageSetEntityDead.class);
        wrapper.registerMessage(MessageSyncTile.class);
        Module.getActiveModules().stream().sorted().forEach(m -> m.registerMessages(wrapper));
    }
}