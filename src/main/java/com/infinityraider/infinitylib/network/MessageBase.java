package com.infinityraider.infinitylib.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.infinitylib.network.serialization.MessageElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base message class implementation for IMessage.
 * This class features easy registering and processing.
 * There are three remarks which should be kept in mind when extending this class:
 *
 * 1) Any extending class needs to have a constructor without arguments,
 *    this is used to create a new instance of the message on the receiving end.
 *
 * 2) Only three methods need to be implemented in an extending class: getMessageHandlerSide(), processMessage() and getReply()
 *     - getMessageHandlerSide should return the side where the message is handled, i.e. where processMessage() is called.
 *     - processMessage() is the method from where the actions have to be called for this method, this method will be run on the correct thread (server or client)
 *     - getReply(), gets a reply message which is automatically sent back to the sender of this message, this is run on the netty thread (do not perform any actions here)
 *
 *  3) Any field declared in extending classes will be synced automatically.
 *     This is done using class reflection on the declared fields.
 *     The process will only work for previously registered serializers (see serialization.IMessageElementReader and serialization.IMessageElementWriter),
 *     The classes registered by default are:
 *      - boolean (and Boolean)
 *      - byte (and Byte)
 *      - short (and Short)
 *      - int (and Integer)
 *      - long (and Long)
 *      - float (and Float)
 *      - double (and Double)
 *      - char (and Character)
 *      - String
 *      - Entity (and all subclasses)
 *      - TileEntity (and all subclasses)
 *      - BlockPos
 *      - Block (and all subclasses)
 *      - Item (and all subclasses)
 *      - ItemStack
 *      - NBTTagCompound
 *      - Vec3d
 *      - ITextComponent (and all subclasses)
 *      - any Enum
 *      - any Array of any valid class (e.g. int[], Entity[], ...)
 *
 *      Only fields with registered types will be successfully synced, if fields are detected which do not have a serializer registered, an error will be logged.
 *      If your message class contains fields with a type not listed above, you have to register a new serializer for this class using INetworkWrapper.registerDataSerializer(),
 *      this method will register serializers for this type as well as an array of this type *
 *
 */
@SuppressWarnings("unused")
public abstract class MessageBase {
    private static final Map<Class<? extends MessageBase>, List<MessageElement>> ELEMENT_MAP = Maps.newIdentityHashMap();
    private static final Map<Class<? extends MessageBase>, INetworkWrapper> WRAPPER_MAP = Maps.newIdentityHashMap();

    private INetworkWrapper wrapper;

    public MessageBase() {
        super();
    }

    /**
     * @return the INetworkWrapper this message is registered to
     */
    public final INetworkWrapper getNetworkWrapper() {
        if (this.wrapper == null) {
            if (WRAPPER_MAP.containsKey(this.getClass())) {
                this.wrapper = WRAPPER_MAP.get(this.getClass());
            } else {
                this.wrapper = NetworkWrapperDummy.getInstance();
            }
        }
        return this.wrapper;
    }

    /**
     * @return the returning side for this message
     */
    public abstract NetworkDirection getMessageDirection();

    /**
     * Called to process this message, only called on the receiving side (returned by getMessageHandlerSide() )
     *
     * @param ctx the message context
     */
    protected abstract void processMessage(NetworkEvent.Context ctx);

    /**
     * Called to register required missing serializers for this class,
     * For a list of default registered serializers, see the list in the javadoc for this class
     * @return a list of IMessageSerializers required to serialize this message
     */
    protected List<IMessageSerializer> getNecessarySerializers() {
        return Collections.emptyList();
    }

    public final <REQ extends MessageBase> REQ fromBytes(PacketBuffer buf) {
        Map<Class<? extends MessageBase>, List<MessageElement>> map = ELEMENT_MAP;
        if (ELEMENT_MAP.containsKey(this.getClass())) {
            for (MessageElement element : ELEMENT_MAP.get(this.getClass())) {
                element.readFromByteBuf(buf, this);
            }
        }
        return (REQ) this;
    }

    @SuppressWarnings("unchecked")
    public final void toBytes(PacketBuffer buf) {
        Map<Class<? extends MessageBase>, List<MessageElement>> map = ELEMENT_MAP;
        if (ELEMENT_MAP.containsKey(this.getClass())) {
            for (MessageElement element : ELEMENT_MAP.get(this.getClass())) {
                element.writeToByteBuf(buf, this);
            }
        }
    }

    /**
     * Sends this message to all connected clients,
     * only valid if this message is handled on the client
     */
    public final MessageBase sendToAll() {
        this.getNetworkWrapper().sendToAll(this);
        return this;
    }

    /**
     * Sends this message to one particular connected client
     * only valid if this message is handled on the client
     */
    public final MessageBase sendTo(PlayerEntity player) {
        if(player instanceof ServerPlayerEntity) {
            this.getNetworkWrapper().sendTo(this, (ServerPlayerEntity) player);
        } else {
            InfinityLib.instance.getLogger().error("Can not send message directly to a player from a client");
        }
        return this;
    }

    /**
     * Sends this message to one particular connected client
     * only valid if this message is handled on the client
     */
    public final MessageBase sendTo(ServerPlayerEntity player) {
        this.getNetworkWrapper().sendTo(this, player);
        return this;
    }

    /**
     * Sends this message to all connected clients near a certain point,
     * only valid if this message is handled on the client
     */
    public final MessageBase sendToAllAround(World world, double x, double y, double z, double range) {
        this.getNetworkWrapper().sendToAllAround(this, world, x, y, z, range);
        return this;
    }

    /**
     * Sends this message to all connected clients near a certain point,
     * only valid if this message is handled on the client
     */
    public final MessageBase sendToAllAround(RegistryKey<World> dimension, double x, double y, double z, double range) {
        this.getNetworkWrapper().sendToAllAround(this, dimension, x, y, z, range);
        return this;
    }

    /**
     * Sends this message to all connected clients near a certain point,
     * only valid if this message is handled on the client
     */
    public final MessageBase sendToAllAround(Supplier<PacketDistributor.TargetPoint> point) {
        this.getNetworkWrapper().sendToAllAround(this, point);
        return this;
    }

    /**
     * Sends this message to all connected clients in a certain dimension,
     * only valid if this message is handled on the client
     */
    public final MessageBase sendToDimension(World world) {
        this.getNetworkWrapper().sendToDimension(this, world);
        return this;
    }

    /**
     * Sends this message to all connected clients in a certain dimension,
     * only valid if this message is handled on the client
     */
    public final MessageBase sendToDimension(RegistryKey<World> dimension) {
        this.getNetworkWrapper().sendToDimension(this, dimension);
        return this;
    }

    /**
     * Sends this message to the server,
     * only valid if this message is handled on the server
     */
    public final MessageBase sendToServer() {
        this.getNetworkWrapper().sendToServer(this);
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public final String getServerId() {
        final ServerData data = Minecraft.getInstance().getCurrentServerData();
        return "server_" + data.serverIP.replaceAll("\\.", "-").replaceAll(":", "_");
    }

    static void onMessageRegistered(Class<? extends MessageBase> clazz, INetworkWrapper wrapper) {
        WRAPPER_MAP.put(clazz, wrapper);
        compileFieldsList(clazz);
    }

    private static void compileFieldsList(Class<? extends MessageBase> clazz) {
        if (!ELEMENT_MAP.containsKey(clazz)) {
            ImmutableList<Field> fields = fetchFieldsRecursively(clazz, ImmutableList.builder());
            List<MessageElement<?>> elements = Lists.newArrayList();
            List<Field> skippedFields = Lists.newArrayList();
            for (Field field : fields) {
                if(Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Optional<MessageElement<?>> element = MessageElement.createNewElement(field);
                if (element.isPresent()) {
                    elements.add(element.get());
                } else {
                    skippedFields.add(field);
                }
            }
            ELEMENT_MAP.put(clazz, ImmutableList.copyOf(elements));
            if (skippedFields.size() > 0) {
                InfinityLib.instance.getLogger().error("SKIPPED FIELDS FOR MESSAGE CLASS: " + clazz.getName());
                InfinityLib.instance.getLogger().error("Report this to the mod author, skipped fields are:");
                for (Field field : skippedFields) {
                    InfinityLib.instance.getLogger().error(" - " + field.getName());
                }
                InfinityLib.instance.getLogger().error("serializers have to be registered for these field types,");
                InfinityLib.instance.getLogger().error("this is done via INetworkWrapper.registerDataSerializer");
            }
        }
    }

    private static ImmutableList<Field> fetchFieldsRecursively(Class<?> clazz, ImmutableList.Builder<Field> builder) {
        if(clazz == null) {
            return builder.build();
        }
        if(!(MessageBase.class.isAssignableFrom(clazz))) {
            return builder.build();
        }
        if(clazz == MessageBase.class) {
            return builder.build();
        }
        builder.add(clazz.getDeclaredFields());
        return fetchFieldsRecursively(clazz.getSuperclass(), builder);
    }
}
