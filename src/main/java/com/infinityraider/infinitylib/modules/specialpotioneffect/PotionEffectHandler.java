package com.infinityraider.infinitylib.modules.specialpotioneffect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PotionEffectHandler {
    private static final PotionEffectHandler INSTANCE = new PotionEffectHandler();

    public static PotionEffectHandler getInstance() {
        return INSTANCE;
    }

    private PotionEffectHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();



        Collection<PotionEffect> potions = entity.getActivePotionEffects();
        List<Potion> specials = potions.stream().filter(p -> p.getPotion() instanceof ISpecialPotion).map(PotionEffect::getPotion).collect(Collectors.toList());
    }
}
