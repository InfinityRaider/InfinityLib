package com.infinityraider.infinitylib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSetEntityDead extends MessageBase<IMessage> {
    private Entity entity;

    public MessageSetEntityDead() {
        super();
    }

    public MessageSetEntityDead(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(ctx.side == Side.CLIENT && this.entity != null && !this.entity.isDead) {
            this.entity.setDead();
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entity = this.readEntityFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writeEntityToByteBuf(buf, this.entity);
    }
}
