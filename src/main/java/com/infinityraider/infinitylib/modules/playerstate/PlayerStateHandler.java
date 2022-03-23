package com.infinityraider.infinitylib.modules.playerstate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerStateHandler {
    private static final PlayerStateHandler INSTANCE = new PlayerStateHandler();

    public static PlayerStateHandler getInstance() {
        return INSTANCE;
    }

    private PlayerStateHandler() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onProjectileImpactEvent(ProjectileImpactEvent event) {
        if(event.getRayTraceResult().getType() == HitResult.Type.ENTITY) {
            Entity hit = ((EntityHitResult) event.getRayTraceResult()).getEntity();
            if(hit instanceof Player) {
                Player player = (Player) hit;
                if (StatusEffect.ETHEREAL.isActive(player)) {
                    event.setCanceled(true);
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onEntityHurtEvent(LivingHurtEvent event) {
        if(event.getEntityLiving() instanceof Player) {
            Player player = (Player) event.getEntityLiving();
            if(StatusEffect.INVULNERABLE.isActive(player)) {
                event.setCanceled(true);
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unused")
    public void onPlayerRenderPreEvent(RenderPlayerEvent.Pre event) {
        this.cancelRenderEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unused")
    public void onPlayerRenderPostEvent(RenderPlayerEvent.Post event) {
        this.cancelRenderEvent(event);
    }

    @OnlyIn(Dist.CLIENT)
    private void cancelRenderEvent(RenderPlayerEvent event) {
        if(StatusEffect.INVISIBLE.isActive(event.getPlayer())) {
            if(event.isCancelable()) {
                event.setCanceled(true);
            }
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onEntityTargetingEvent(LivingSetAttackTargetEvent event) {
        LivingEntity target = event.getTarget();
        LivingEntity attacker = event.getEntityLiving();
        if(target == null || attacker == null || !(target instanceof Player) || !(attacker instanceof LivingEntity)) {
            return;
        }
        if(StatusEffect.UNDETECTABLE.isActive((Player) target)) {
            if (attacker instanceof Mob) {
                Mob mob = (Mob) attacker;
                mob.setAggressive(false);
                mob.setTarget(null);
            }
            attacker.setLastHurtMob(null);
        }
    }

}
