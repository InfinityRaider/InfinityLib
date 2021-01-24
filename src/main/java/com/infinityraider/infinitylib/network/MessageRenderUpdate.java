package com.infinityraider.infinitylib.network;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageRenderUpdate extends MessageBase {
    private BlockPos pos;

    public MessageRenderUpdate() {
        super();
    }

    public MessageRenderUpdate(BlockPos pos) {
        this();
        this.pos = pos;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if (this.pos != null) {
            InfinityLib.instance.proxy().forceClientRenderUpdate(this.pos);
        }
    }
}
