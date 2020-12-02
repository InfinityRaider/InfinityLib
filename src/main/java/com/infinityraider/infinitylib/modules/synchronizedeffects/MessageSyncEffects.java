package com.infinityraider.infinitylib.modules.synchronizedeffects;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSyncEffects extends MessageBase {
    private Entity entity;
    private CompoundNBT nbt;

    public MessageSyncEffects() {
        super();
    }

    public MessageSyncEffects(EffectTracker tracker) {
        this();
        this.entity = tracker.getEntity();
        this.nbt = tracker.writeToNBT();
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.entity instanceof LivingEntity && this.nbt != null) {
            EffectTracker tracker = CapabilityEffectTracker.getEffectTracker((LivingEntity) this.entity);
            if(tracker != null) {
                tracker.readFromNBT(this.nbt);
            }
        }
    }
}
