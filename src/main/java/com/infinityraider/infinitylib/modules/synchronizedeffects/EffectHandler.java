package com.infinityraider.infinitylib.modules.synchronizedeffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;
import java.util.stream.Collectors;

public class EffectHandler {
    private static final EffectHandler INSTANCE = new EffectHandler();

    public static EffectHandler getInstance() {
        return INSTANCE;
    }

    private EffectHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if(entity.getEntityWorld().isRemote) {
            return;
        }
        EffectTracker tracker = CapabilityEffectTracker.getEffectTracker(entity);
        if(tracker == null) {
            return;
        }
        Collection<EffectInstance> potions = entity.getActivePotionEffects();
        tracker.updatePotionEffects(potions.stream()
                .filter(p -> p.getPotion() instanceof ISynchronizedEffect)
                .map(EffectInstance::getPotion).collect(Collectors.toList()));
    }
}
