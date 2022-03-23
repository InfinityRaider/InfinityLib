package com.infinityraider.infinitylib.modules.synchronizedeffects;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageSyncEffects extends MessageBase {
    private Entity entity;
    private CompoundTag nbt;

    public MessageSyncEffects() {
        super();
    }

    public MessageSyncEffects(EffectTracker tracker) {
        this();
        this.entity = tracker.getEntity();
        this.nbt = tracker.serializeNBT();
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
                tracker.deserializeNBT(this.nbt);
            }
        }
    }
}
