package com.infinityraider.infinitylib.modules.playerstate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStateHandler {
    private static final PlayerStateHandler INSTANCE = new PlayerStateHandler();

    public static PlayerStateHandler getInstance() {
        return INSTANCE;
    }

    private final HashMap<UUID, State> states;

    private PlayerStateHandler() {
        this.states = new HashMap<>();
    }

    State getState(PlayerEntity player) {
        if(!states.containsKey(player.getUniqueID())) {
            states.put(player.getUniqueID(), new State(player));
        }
        return states.get(player.getUniqueID());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onProjectileImpactEvent(ProjectileImpactEvent event) {
        if(event.getRayTraceResult().getType() == RayTraceResult.Type.ENTITY) {
            Entity hit = ((EntityRayTraceResult) event.getRayTraceResult()).getEntity();
            if(hit instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) hit;
                if (getState(player).isEthereal()) {
                    event.setCanceled(true);
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onEntityHurtEvent(LivingHurtEvent event) {
        if(event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if(getState(player).isInvulnerable()) {
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
        if(getState(event.getPlayer()).isInvisible()) {
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
        if(target == null || attacker == null || !(target instanceof PlayerEntity) || !(attacker instanceof LivingEntity)) {
            return;
        }
        if(getState((PlayerEntity) target).isUndetectable()) {
            attacker.setRevengeTarget(null);
        }
    }

}
