package com.infinityraider.infinitylib.modules.synchronizedeffects;

import com.google.common.primitives.Ints;
import com.infinityraider.infinitylib.reference.Names;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;

import java.util.*;
import java.util.stream.Collectors;

public class EffectTracker implements ISerializable {
    private final LivingEntity entity;

    private Set<Integer> activeEffects;

    protected EffectTracker(LivingEntity entity) {
        this.activeEffects = new HashSet<>();
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void updatePotionEffects(List<Effect> effects) {
        boolean update = false;
        //remove previously active effects from the effect list
        Iterator<Integer> it = activeEffects.iterator();
        while(it.hasNext()) {
            int id = it.next();
            Effect effect = Effect.get(id);
            if(!effects.contains(effect)) {
                it.remove();
                update = true;
            } else {
                effects.remove(effect);
            }
        }
        //add newly activated effects to the effect list
        if(effects.size() > 0) {
            activeEffects.addAll(effects.stream().map(Effect::getId).collect(Collectors.toList()));
            update = true;
        }
        //send changes to the client
        if(update) {
            this.syncToClient();
        }
    }

    public List<Effect> getActiveEffects() {
        return this.activeEffects.stream().map(Effect::get).collect(Collectors.toList());
    }

    protected void syncToClient() {
        new MessageSyncEffects(this).sendToAll();
    }

    @Override
    public void readFromNBT(CompoundNBT tag) {
        this.activeEffects.clear();
        if(tag.contains(Names.NBT.EFFECTS)) {
            this.activeEffects = Arrays.stream(tag.getIntArray(Names.NBT.EFFECTS)).boxed().collect(Collectors.toSet());
        }
    }

    @Override
    public CompoundNBT writeToNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putIntArray(Names.NBT.EFFECTS, Ints.toArray(this.activeEffects));
        return tag;
    }
}
