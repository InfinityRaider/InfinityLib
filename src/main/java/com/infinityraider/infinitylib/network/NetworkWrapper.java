package com.infinityraider.infinitylib.network;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.network.serialization.IMessageReader;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.infinitylib.network.serialization.IMessageWriter;
import com.infinityraider.infinitylib.network.serialization.MessageSerializerStore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class NetworkWrapper implements INetworkWrapper {
    private static final String PROTOCOL_VERSION = "1";

    private final InfinityMod<?,?> mod;

    private SimpleChannel channel;
    private int nextId = 0;

    public NetworkWrapper(InfinityMod<?,?> mod) {
        this.mod = mod;
    }

    public void init() {
        this.channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(mod.getModId(), "network_channel"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals);
        this.mod.registerMessages(this);
    }

    @Override
    public void sendToAll(MessageBase message) {
        if(message.getMessageDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            this.channel.send(PacketDistributor.ALL.noArg(), message);
        }
    }

    @Override
    public void sendTo(MessageBase message, ServerPlayer player) {
        if(message.getMessageDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            this.channel.send(PacketDistributor.PLAYER.with(() -> player), message);
        }
    }

    @Override
    public void sendToAllAround(MessageBase message, Level world, double x, double y, double z, double range) {
        this.sendToAllAround(message, world.dimension(), x, y, z, range);
    }

    @Override
    public void sendToAllAround(MessageBase message, ResourceKey<Level> dimension, double x, double y, double z, double range) {
        if(message.getMessageDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            this.sendToAllAround(message, PacketDistributor.TargetPoint.p(x, y, z, range, dimension));
        }
    }

    @Override
    public void sendToAllAround(MessageBase message, Supplier<PacketDistributor.TargetPoint> point) {
        if(message.getMessageDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            this.channel.send(PacketDistributor.NEAR.with(point), message);
        }
    }

    @Override
    public void sendToDimension(MessageBase message, Level world) {
        this.sendToDimension(message, world.dimension());
    }

    @Override
    public void sendToDimension(MessageBase message, ResourceKey<Level> dimension) {
        if(message.getMessageDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            this.channel.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
        }
    }

    @Override
    public void sendToServer(MessageBase message) {
        if(message.getMessageDirection() == NetworkDirection.PLAY_TO_SERVER) {
            this.channel.send(PacketDistributor.SERVER.noArg(), message);
        }
    }

    @Override
    public <MSG extends MessageBase> void registerMessage(Class<MSG> msgClass) {
        try {
            // Fetch constructor and create an instance
            Constructor<MSG> msgConstructor = msgClass.getDeclaredConstructor();
            MSG msg = msgConstructor.newInstance();
            // Register required data serializers
            msg.getNecessarySerializers().stream().forEach(this::registerDataSerializer);
            // Register the message
            channel.registerMessage(nextId,
                    msgClass,
                    new MessageEncoder<>(),
                    new MessageDecoder<>(msgConstructor),
                    new MessageHandler<>(),
                    Optional.ofNullable(msg.getMessageDirection())
            );
            InfinityLib.instance.getLogger().debug("Registered message \"" + msgClass.getName() + "\" with id " + nextId);
            // Increment ID
            nextId = nextId + 1;
            MessageBase.onMessageRegistered(msgClass, this);
        } catch (Exception e) {
            InfinityLib.instance.getLogger().printStackTrace(e);
        }
    }

    @Override
    public <T> void registerDataSerializer(Class<T> clazz, IMessageWriter<T> writer, IMessageReader<T> reader) {
        MessageSerializerStore.registerMessageSerializer(clazz, writer, reader);
    }

    @Override
    public <T> void registerDataSerializer(IMessageSerializer<T> serializer) {
        MessageSerializerStore.registerMessageSerializer(serializer);
    }

    private static class MessageEncoder<MSG extends MessageBase> implements BiConsumer<MSG, FriendlyByteBuf> {
        private MessageEncoder() {}

        @Override
        public void accept(MSG req, FriendlyByteBuf packetBuffer) {
            req.toBytes(packetBuffer);
        }
    }

    private static class MessageDecoder<MSG extends MessageBase> implements Function<FriendlyByteBuf, MSG> {
        private final Constructor<MSG> msgConstructor;

        private MessageDecoder(Constructor<MSG> msgConstructor) {
            this.msgConstructor = msgConstructor;
        }

        @Override
        public MSG apply(FriendlyByteBuf buf) {
            try {
                return this.msgConstructor.newInstance().fromBytes(buf);
            } catch (Exception e) {
                InfinityLib.instance.getLogger().printStackTrace(e);
            }
            return null;
        }
    }

    private static final class MessageHandler<MSG extends MessageBase> implements BiConsumer<MSG, Supplier<NetworkEvent.Context>> {
        private MessageHandler() {}

        @Override
        public void accept(MSG msg, Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(new MessageTask(msg, ctxSupplier.get()));
        }
    }

    private static class MessageTask implements Runnable {
        private final MessageBase message;
        private final NetworkEvent.Context ctx;

        private MessageTask(MessageBase message, NetworkEvent.Context ctx) {
            this.message = message;
            this.ctx = ctx;
        }

        @Override
        public void run() {
            if(this.message.getMessageDirection() == this.ctx.getDirection()) {
                this.message.processMessage(this.ctx);
            }
            ctx.setPacketHandled(true);
        }
    }
}
