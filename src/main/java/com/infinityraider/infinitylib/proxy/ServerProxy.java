package com.infinityraider.infinitylib.proxy;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.proxy.base.IServerProxyBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
@SideOnly(Side.SERVER)
public class ServerProxy implements IProxy, IServerProxyBase {
    @Override
    public void registerRenderers(InfinityMod mod) {}
}
