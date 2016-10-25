package com.infinityraider.infinitylib.network;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.InfinityMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public class NetworkWrapper implements INetworkWrapper {
    private final SimpleNetworkWrapper wrapper;
    private int nextId = 0;

    public NetworkWrapper(InfinityMod mod) {
        String id = mod.getModId();
        if(id.length() > 20) {
            id = id.substring(0, 20);
        }
        this.wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(id);
    }

    @Override
    public void sendToAll(MessageBase message) {
        this.wrapper.sendToAll(message);
    }

    @Override
    public void sendTo(MessageBase message, EntityPlayerMP player) {
        this.wrapper.sendTo(message, player);
    }

    @Override
    public void sendToAllAround(MessageBase message, World world, double x, double y, double z, double range) {
        this.sendToAllAround(message, world.provider.getDimension(), x, y, z, range);
    }

    @Override
    public void sendToAllAround(MessageBase message, int dimension, double x, double y, double z, double range) {
        this.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
    }

    @Override
    public void sendToAllAround(MessageBase message, NetworkRegistry.TargetPoint point) {
        this.wrapper.sendToAllAround(message, point);
    }

    @Override
    public void sendToDimension(MessageBase messageBase, World world) {
        this.sendToDimension(messageBase, world.provider.getDimension());
    }

    @Override
    public void sendToDimension(MessageBase message, int dimensionId) {
        this.wrapper.sendToDimension(message, dimensionId);
    }

    @Override
    public void sendToServer(MessageBase message) {
        this.wrapper.sendToServer(message);
    }

    @Override
    public <REQ extends MessageBase<REPLY>, REPLY extends IMessage> void registerMessage(Class<? extends REQ> message) {
        try {
            Side side = message.getDeclaredConstructor().newInstance().getMessageHandlerSide();
            wrapper.registerMessage(new MessageHandler<REQ, REPLY>(), message, nextId, side);
            InfinityLib.instance.getLogger().debug("Registered message \"" + message.getName() + "\" with id " + nextId);
            nextId = nextId + 1;
        } catch (Exception e) {
            InfinityLib.instance.getLogger().printStackTrace(e);
        }
    }

    private static final class MessageHandler<REQ extends MessageBase<REPLY>, REPLY extends IMessage> implements IMessageHandler<REQ, REPLY> {
        protected MessageHandler() {
        }

        @Override
        public final REPLY onMessage(REQ message, MessageContext ctx) {
            InfinityLib.proxy.queueTask(new MessageTask(message, ctx));
            return message.getReply(ctx);
        }
    }

    private static class MessageTask implements Runnable {
        private final MessageBase message;
        private final MessageContext ctx;

        private MessageTask(MessageBase message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
        }

        @Override
        public void run() {
            this.message.processMessage(this.ctx);
        }
    }
}
