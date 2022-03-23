package com.infinityraider.infinitylib.modules.synchronizedeffects;

import com.google.common.primitives.Ints;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation.Serializable;
import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;
import java.util.stream.Collectors;

public class EffectTracker implements Serializable<EffectTracker> {
    private final LivingEntity entity;

    private Set<Integer> activeEffects;

    protected EffectTracker(LivingEntity entity) {
        this.activeEffects = new HashSet<>();
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void updatePotionEffects(List<MobEffect> effects) {
        boolean update = false;
        //remove previously active effects from the effect list
        Iterator<Integer> it = activeEffects.iterator();
        while(it.hasNext()) {
            int id = it.next();
            MobEffect effect = MobEffect.byId(id);
            if(!effects.contains(effect)) {
                it.remove();
                update = true;
            } else {
                effects.remove(effect);
            }
        }
        //add newly activated effects to the effect list
        if(effects.size() > 0) {
            activeEffects.addAll(effects.stream().map(MobEffect::getId).collect(Collectors.toList()));
            update = true;
        }
        //send changes to the client
        if(update) {
            this.syncToClient();
        }
    }

    public List<MobEffect> getActiveEffects() {
        return this.activeEffects.stream().map(MobEffect::byId).collect(Collectors.toList());
    }

    protected void syncToClient() {
        new MessageSyncEffects(this).sendToAll();
    }

    @Override
    public void copyDataFrom(EffectTracker from) {
        this.activeEffects.clear();
        this.activeEffects.addAll(from.activeEffects);
        this.syncToClient();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.activeEffects.clear();
        if(tag.contains(Names.NBT.EFFECTS)) {
            this.activeEffects = Arrays.stream(tag.getIntArray(Names.NBT.EFFECTS)).boxed().collect(Collectors.toSet());
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray(Names.NBT.EFFECTS, Ints.toArray(this.activeEffects));
        return tag;
    }
}
