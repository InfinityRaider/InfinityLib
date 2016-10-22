package com.infinityraider.infinitylib.modules.specialpotioneffect;

import com.infinityraider.infinitylib.network.MessageBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSyncPotions extends MessageBase<IMessage> {
    private EntityLivingBase entity;
    private NBTTagCompound nbt;

    public MessageSyncPotions() {
        super();
    }

    public MessageSyncPotions(PotionTracker tracker) {
        this();
        this.entity = tracker.getEntity();
        this.nbt = tracker.writeToNBT();
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(ctx.side == Side.CLIENT && this.entity != null && this.nbt != null) {
            PotionTracker tracker = CapabilityPotionTracker.getPotionTracker(this.entity);
            if(tracker != null) {
                tracker.readFromNBT(this.nbt);
            }
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        Entity entity = this.readEntityFromByteBuf(buf);
        if(entity instanceof EntityLivingBase) {
            this.entity = (EntityLivingBase) entity;
            this.nbt = this.readNBTFromByteBuf(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writeEntityToByteBuf(buf, this.entity);
        this.writeNBTToByteBuf(buf, this.nbt);
    }
}
