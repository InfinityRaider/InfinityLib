package com.infinityraider.infinitylib.modules.synchronizedeffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
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
        if(entity.getLevel().isClientSide()) {
            return;
        }
        EffectTracker tracker = CapabilityEffectTracker.getEffectTracker(entity);
        if(tracker == null) {
            return;
        }
        Collection<MobEffectInstance> potions = entity.getActiveEffects();
        tracker.updatePotionEffects(potions.stream()
                .filter(p -> p.getEffect() instanceof ISynchronizedEffect)
                .map(MobEffectInstance::getEffect)
                .collect(Collectors.toList()));
    }
}
