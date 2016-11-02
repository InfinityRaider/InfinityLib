package com.infinityraider.infinitylib.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.network.serialization.MessageElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
 *      - Entity
 *      - EntityPlayer
 *      - TileEntity
 *      - BlockPos
 *      - Block
 *      - Item
 *      - ItemStack
 *      - NBTTagCompound
 *      - arrays of all of the above (e.g. int[], Entity[], ...)
 *
 *      Only fields with registered types will be successfully synced, if fields are detected which do not have a serializer registered, an error will be logged.
 *      If your message class contains fields with a type not listed above, you have to register a new serializer for this class using INetworkWrapper.registerDataSerializer(),
 *      this method will register serializers for this type as well as an array of this type *
 *
 * @param <REPLY> the generic type of the reply sent by this message, can be IMessage if no reply is expected
 */
@SuppressWarnings("unused")
public abstract class MessageBase<REPLY extends IMessage> implements IMessage {
    private static final Map<Class<? extends MessageBase>, List<Pair<Field, MessageElement>>> FIELD_MAP = Maps.newHashMap();
    private static final Map<Class<? extends MessageBase>, INetworkWrapper> WRAPPER_MAP = Maps.newHashMap();

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
    public abstract Side getMessageHandlerSide();

    /**
     * Called to process this message, only called on the receiving side (returned by getMessageHandlerSide() )
     *
     * @param ctx the message context
     */
    protected abstract void processMessage(MessageContext ctx);

    /**
     * Called to get a reply message to be sent back to the original sender automatically
     *
     * @param ctx the message context
     * @return a new message, or null if nothing has to be sent back
     */
    protected abstract REPLY getReply(MessageContext ctx);

    /**
     * Sends this message to all connected clients,
     * only valid if this message is handled on the client
     */
    public final MessageBase<REPLY> sendToAll() {
        this.getNetworkWrapper().sendToAll(this);
        return this;
    }

    /**
     * Sends this message to one particular connected client
     * only valid if this message is handled on the client
     */
    public final MessageBase<REPLY> sendTo(EntityPlayerMP player) {
        this.getNetworkWrapper().sendTo(this, player);
        return this;
    }

    /**
     * Sends this message to all connected clients near a certain point,
     * only valid if this message is handled on the client
     */
    public final MessageBase<REPLY> sendToAllAround(World world, double x, double y, double z, double range) {
        this.getNetworkWrapper().sendToAllAround(this, world.provider.getDimension(), x, y, z, range);
        return this;
    }

    /**
     * Sends this message to all connected clients near a certain point,
     * only valid if this message is handled on the client
     */
    public final MessageBase<REPLY> sendToAllAround(int dimension, double x, double y, double z, double range) {
        this.getNetworkWrapper().sendToAllAround(this, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
        return this;
    }

    /**
     * Sends this message to all connected clients near a certain point,
     * only valid if this message is handled on the client
     */
    public final MessageBase<REPLY> sendToAllAround(NetworkRegistry.TargetPoint point) {
        this.getNetworkWrapper().sendToAllAround(this, point);
        return this;
    }

    /**
     * Sends this message to all connected clients in a certain dimension,
     * only valid if this message is handled on the client
     */
    public final MessageBase<REPLY> sendToDimension(World world) {
        this.getNetworkWrapper().sendToDimension(this, world.provider.getDimension());
        return this;
    }

    /**
     * Sends this message to all connected clients in a certain dimension,
     * only valid if this message is handled on the client
     */
    public final MessageBase<REPLY> sendToDimension(int dimensionId) {
        this.getNetworkWrapper().sendToDimension(this, dimensionId);
        return this;
    }

    /**
     * Sends this message to the server,
     * only valid if this message is handled on the server
     */
    public final MessageBase<REPLY> sendToServer(MessageBase message) {
        this.getNetworkWrapper().sendToServer(message);
        return this;
    }

    @Override
    public final void fromBytes(ByteBuf buf) {
        if (FIELD_MAP.containsKey(this.getClass())) {
            for (Pair<Field, MessageElement> pair : FIELD_MAP.get(this.getClass())) {
                boolean shouldRead = buf.readBoolean();
                if (shouldRead) {
                    Object object = pair.getRight().readFromByteBuf(buf);
                    if (object != null) {
                        try {
                            pair.getLeft().set(this, object);
                        } catch (IllegalAccessException e) {
                            InfinityLib.instance.getLogger().error("Failed setting field data");
                            InfinityLib.instance.getLogger().error(e.toString());
                        }
                    } else {
                        InfinityLib.instance.getLogger().error("Object was null, did not set field " + pair.getLeft().getName());
                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void toBytes(ByteBuf buf) {
        if (FIELD_MAP.containsKey(this.getClass())) {
            for (Pair<Field, MessageElement> pair : FIELD_MAP.get(this.getClass())) {
                Object object = null;
                try {
                    object = pair.getLeft().get(this);
                } catch(IllegalAccessException e) {
                    InfinityLib.instance.getLogger().error("Failed getting field data");
                    InfinityLib.instance.getLogger().error(e.toString());
                }
                buf.writeBoolean(object != null);
                if (object != null) {
                    pair.getRight().writeToByteBuf(buf, object);
                }
            }
        }
    }

    static void onMessageRegistered(Class<? extends MessageBase> clazz, INetworkWrapper wrapper) {
        WRAPPER_MAP.put(clazz, wrapper);
        compileFieldsList(clazz);
    }

    private static void compileFieldsList(Class<? extends MessageBase> clazz) {
        if (!FIELD_MAP.containsKey(clazz)) {
            Field[] fields = clazz.getDeclaredFields();
            List<Pair<Field, MessageElement>> elements = Lists.newArrayList();
            List<Field> skippedFields = Lists.newArrayList();
            for (Field field : fields) {
                field.setAccessible(true);
                Optional<MessageElement> element = MessageElement.getMessageElement(field);
                if (element.isPresent()) {
                    elements.add(Pair.of(field, element.get()));
                } else {
                    skippedFields.add(field);
                }
            }
            FIELD_MAP.put(clazz, ImmutableList.copyOf(elements));
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
}
