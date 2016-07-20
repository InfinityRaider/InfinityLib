package com.infinityraider.infinitylib.proxy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy implements IServerProxyBase {
    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();
    }
}
