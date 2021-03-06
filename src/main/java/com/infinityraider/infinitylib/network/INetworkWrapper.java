package com.infinityraider.infinitylib.network;

import com.infinityraider.infinitylib.network.serialization.IMessageReader;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.infinitylib.network.serialization.IMessageWriter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface INetworkWrapper {

    /**
     * Sends a message to all connected clients,
     * only valid if the message is handled on the client
     */
    void sendToAll(MessageBase message);

    /**
     * Sends a message to one particular connected client
     * only valid if the message is handled on the client
     */
    void sendTo(MessageBase message, ServerPlayerEntity player);

    /**
     * Sends a message to all connected clients near a certain point,
     * only valid if the message is handled on the client
     */
    void sendToAllAround(MessageBase message, World world, double x, double y, double z, double range);

    /**
     * Sends a message to all connected clients near a certain point,
     * only valid if the message is handled on the client
     */
    void sendToAllAround(MessageBase message, RegistryKey<World> dimension, double x, double y, double z, double range);

    /**
     * Sends a message to all connected clients near a certain point,
     * only valid if the message is handled on the client
     */
    void sendToAllAround(MessageBase message, Supplier<PacketDistributor.TargetPoint> point);

    /**
     * Sends a message to all connected clients in a certain dimension,
     * only valid if the message is handled on the client
     */
    void sendToDimension(MessageBase message, World world);

    /**
     * Sends a message to all connected clients in a certain dimension,
     * only valid if the message is handled on the client
     */
    void sendToDimension(MessageBase message, RegistryKey<World> dimension);

    /**
     * Sends a message to the server,
     * only valid if the message is handled on the server
     */
    void sendToServer(MessageBase message);

    /**
     * Registers a MessageBase to this wrapper
     * @param message a constructor of the message to register
     * @param <REQ> the generic type of the message
     */
    <REQ extends MessageBase> void registerMessage(Class<REQ> message);

    /**
     * Registers a serializer for a class type, this method will also register the array type for this type (unless the type is an array itself).
     * By default the following classes (and their arrays) are registered:
     *  - boolean (and Boolean)
     *  - byte (and Byte)
     *  - short (and Short)
     *  - int (and Integer)
     *  - long (and Long)
     *  - float (and Float)
     *  - double (and Double)
     *  - char (and Character)
     *  - String
     *  - Entity (and all subclasses)
     *  - TileEntity (and all subclasses)
     *  - BlockPos
     *  - Block (and all subclasses)
     *  - Item (and all subclasses)
     *  - ItemStack
     *  - NBTTagCompound
     *  - Vec3d
     *  - ITextComponent (and all subclasses)
     *  - any Enum
     *  - any Array of a valid class
     *
     * @param clazz type to be registered
     * @param writer data writer for this type, should be the dual of reader (T -> ByteBuf)
     * @param reader data reader for this type, should be the dual of writer (ByteBuf -> T)
     * @param <T> the type for the serializer
     */
    <T> void registerDataSerializer(Class<T>  clazz, IMessageWriter<T> writer, IMessageReader<T> reader);

    /**
     * Does the same as the above method
     * @param serializer the serializer
     * @param <T> the type for the serializer
     */
    <T> void registerDataSerializer(IMessageSerializer<T> serializer);
}
