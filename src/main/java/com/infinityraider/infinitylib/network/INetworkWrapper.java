package com.infinityraider.infinitylib.network;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@SuppressWarnings("unused")
public interface INetworkWrapper {

    void sendToAll(MessageBase message);

    void sendTo(MessageBase message, EntityPlayerMP player);

    void sendToAllAround(MessageBase message, World world, double x, double y, double z, double range);

    void sendToAllAround(MessageBase message, int dimension, double x, double y, double z, double range);

    void sendToAllAround(MessageBase message, NetworkRegistry.TargetPoint point);

    void sendToDimension(MessageBase messageBase, World world);

    void sendToDimension(MessageBase message, int dimensionId);

    void sendToServer(MessageBase message);

    <REQ extends MessageBase<REPLY>, REPLY extends IMessage> void registerMessage(Class<? extends REQ> message);
}
