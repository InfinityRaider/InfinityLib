package com.infinityraider.infinitylib.modules.dualwield;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Collections;
import java.util.List;

public class ModuleDualWield extends Module {
    private static final ModuleDualWield INSTANCE = new ModuleDualWield();

    public static ModuleDualWield getInstance() {
        return INSTANCE;
    }

    private ModuleDualWield() {}

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageAttackDualWielded.class);
        wrapper.registerMessage(MessageMouseButtonPressed.class);
        wrapper.registerMessage(MessageSwingArm.class);
    }

    @Override
    public List<Object> getCommonEventHandlers() {
        return Collections.emptyList();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Object> getClientEventHandlers() {
        return ImmutableList.of(
                MouseClickHandler.getInstance(),
                ArmSwingHandler.getInstance());
    }

    /**
     * Copied from EntityPlayer to mimic attacking behaviour, but with the correct ItemStack and method overrides
     *
     * @param player player attacking
     * @param targetEntity target being attacked
     * @param stack ItemStack used to attack
     * @param hand hand used to attack
     */
    public void attackTargetEntityWithCurrentItem(Player player, Entity targetEntity, IDualWieldedWeapon weapon, ItemStack stack, InteractionHand hand) {
        //Forge hook (of course)
        //---------------------
        if (!ForgeHooks.onPlayerAttackTarget(player, targetEntity)) {
            return;
        }

        //Determine if entity should be attacked and all the attack's properties (damage, knockback, crit, ...)
        //-----------------------------------------------------------------------------------------------------
        if (targetEntity.isAttackable()) {
            if (!targetEntity.skipAttackInteraction(player)) {
                //base damage
                float dmg = (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                //enchantment modifier
                float mdf_ench;
                if (targetEntity instanceof LivingEntity) {
                    mdf_ench = EnchantmentHelper.getDamageBonus(stack, ((LivingEntity)targetEntity).getMobType());
                } else {
                    mdf_ench = EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
                }
                //cooldown modifier
                float mdf_cd = player.getAttackStrengthScale(0.5F);
                dmg = dmg * (0.2F + mdf_cd * mdf_cd * 0.8F);
                mdf_ench = mdf_ench * mdf_cd;
                player.resetAttackStrengthTicker();
                //critical hit & knockback
                if (dmg > 0.0F || mdf_ench > 0.0F) {
                    boolean crit = mdf_cd > 0.9F;
                    boolean sprint_crit = false;
                    int knockBack = 0;
                    knockBack = knockBack + EnchantmentHelper.getKnockbackBonus(player);
                    if (player.isSprinting() && crit) {
                        player.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, player.getSoundSource(), 1.0F, 1.0F);
                        ++knockBack;
                        sprint_crit = true;
                    }
                    boolean doCrit = crit
                            && player.fallDistance > 0.0F
                            && !player.isOnGround() &&
                            !player.onClimbable() &&
                            !player.isInWater() &&
                            !player.hasEffect(MobEffects.BLINDNESS)
                            && !player.isPassenger()
                            && (targetEntity instanceof LivingEntity);
                    doCrit = doCrit && !player.isSprinting();
                    if (doCrit) {
                        dmg *= 1.5F;
                    }
                    dmg = dmg + mdf_ench;
                    //Area of effect knockback
                    boolean aoe = false;
                    double d0 = player.walkDist - player.walkDistO;
                    if (crit && !doCrit && !sprint_crit && player.isOnGround() && d0 < (double) player.getSpeed()) {
                        aoe = true;
                    }
                    //Fire aspect
                    float targetHealthBefore = 0.0F;
                    boolean setFire = false;
                    int fireAspect = EnchantmentHelper.getFireAspect(player);

                    if (targetEntity instanceof LivingEntity) {
                        targetHealthBefore = ((LivingEntity)targetEntity).getHealth();
                        if (fireAspect > 0 && !targetEntity.isOnFire()) {
                            setFire = true;
                            targetEntity.setSecondsOnFire(1);
                        }
                    }
                    Vec3 v = targetEntity.getDeltaMovement();
                    boolean doDamage = targetEntity.hurt(DamageSource.playerAttack(player), dmg);

                    //Apply the damage and effects
                    //----------------------------
                    if (doDamage) {
                        //apply knockback
                        if (knockBack > 0) {
                            if (targetEntity instanceof LivingEntity) {
                                ((LivingEntity)targetEntity).knockback(
                                        (float) knockBack * 0.5F,
                                        (double) Mth.sin(player.getYRot() * 0.017453292F),
                                        (double)(-Mth.cos(player.getYRot() * 0.017453292F)));
                            } else {
                                targetEntity.push(
                                        (double)(-Mth.sin(player.getYRot() * 0.017453292F) * (float)knockBack * 0.5F),
                                        0.1D,
                                        (double)(Mth.cos(player.getYRot() * 0.017453292F) * (float)knockBack * 0.5F));
                            }
                            player.setDeltaMovement(v.multiply(0.6, 1.0, 0.6));
                            player.setSprinting(false);
                        }
                        //apply aoe
                        if (aoe) {
                            float aoe_dmg = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * dmg;
                            player.getLevel().getEntitiesOfClass(LivingEntity.class, player.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(player, targetEntity)).stream()
                                    .filter(e -> e != player
                                            && e != targetEntity
                                            && !player.isAlliedTo(e)
                                            && (!(e instanceof ArmorStand) || !((ArmorStand)e).isMarker())
                                            && player.distanceToSqr(e) < 9.0D)
                                    .forEach(e -> {
                                        e.knockback(0.4F, (double) Mth.sin(player.getYRot() * 0.017453292F), (double) (-Mth.cos(player.getYRot() * 0.017453292F)));
                                        e.hurt(DamageSource.playerAttack(player), aoe_dmg);
                                    });
                            player.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
                            player.sweepAttack();
                        }
                        if (targetEntity instanceof ServerPlayer && targetEntity.hurtMarked) {
                            ((ServerPlayer) targetEntity).connection.send(new ClientboundSetEntityMotionPacket(targetEntity));
                            targetEntity.hurtMarked = false;
                            targetEntity.setDeltaMovement(v);
                        }
                        //play sounds
                        if (doCrit) {
                            player.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0F, 1.0F);
                            player.crit(targetEntity);
                        }
                        if (!doCrit && !aoe) {
                            if (crit) {
                                player.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, player.getSoundSource(), 1.0F, 1.0F);
                            }
                            else {
                                player.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, player.getSoundSource(), 1.0F, 1.0F);
                            }
                        }
                        // enchantment modifier
                        if (mdf_ench > 0.0F) {
                            player.magicCrit(targetEntity);
                        }
                        //Effectiveness versus shields
                        if (!player.getLevel().isClientSide() && targetEntity instanceof Player && weapon.isEffectiveAgainstShield()) {
                            Player targetPlayer = (Player) targetEntity;
                            ItemStack targetHeldItem = targetPlayer.isUsingItem() ? targetPlayer.getUseItem() : null;
                            if (targetHeldItem != null && targetHeldItem.getItem() == Items.SHIELD) {
                                float breakBlockChance = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(player) * 0.05F;
                                if (sprint_crit) {
                                    breakBlockChance += 0.75F;
                                }
                                if (player.getLevel().getRandom().nextFloat() < breakBlockChance) {
                                    targetPlayer.getCooldowns().addCooldown(Items.SHIELD, 100);
                                    player.getLevel().broadcastEntityEvent(targetPlayer, (byte)30);
                                }
                            }
                        }
                        //Don't forget this...
                        player.setLastHurtMob(targetEntity);
                        //Thorns enchantment (I'm starting to hate this method more and more...)
                        if (targetEntity instanceof LivingEntity) {
                            EnchantmentHelper.doPostHurtEffects((LivingEntity) targetEntity, player);
                        }
                        //Bane of arthropods
                        EnchantmentHelper.doPostDamageEffects(player, targetEntity);
                        //Ender dragon logic... really?
                        Entity target = targetEntity;
                        if (targetEntity instanceof PartEntity) {
                            target = ((PartEntity<?>) targetEntity).getParent();
                        }
                        //Set stack to null if stack size becomes zero
                        if (!player.getLevel().isClientSide() && !stack.isEmpty() && target instanceof LivingEntity) {
                            ItemStack copy = stack.copy();
                            stack.hurtEnemy((LivingEntity) target, player);
                            if (stack.isEmpty()) {
                                ForgeEventFactory.onPlayerDestroyItem(player, copy, InteractionHand.MAIN_HAND);
                                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }
                        //Stat tracking
                        if (targetEntity instanceof LivingEntity) {
                            float damageDone = targetHealthBefore - ((LivingEntity)targetEntity).getHealth();
                            player.awardStat(Stats.DAMAGE_DEALT, Math.round(damageDone * 10.0F));
                            if (fireAspect > 0) {
                                targetEntity.setSecondsOnFire(fireAspect * 4);
                            }
                            if (player.getLevel() instanceof ServerLevel && damageDone > 2.0F) {
                                int k = (int)((double)damageDone * 0.5D);
                                ((ServerLevel) player.getLevel()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, targetEntity.getX(), targetEntity.getY(0.5D), targetEntity.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }
                        //Add exhaustion
                        player.causeFoodExhaustion(0.1F);
                    } else {
                        player.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, player.getSoundSource(), 1.0F, 1.0F);
                        if (setFire) {
                            targetEntity.clearFire();
                        }
                    }
                }
            }
        }
    }
}
