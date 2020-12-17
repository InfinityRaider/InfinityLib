package com.infinityraider.infinitylib.modules.dualwield;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void postInitClient() {
        ModelPlayerCustomized.replaceOldModel();
    }

    /**
     * Copied from EntityPlayer to mimic attacking behaviour, but with the correct ItemStack and method overrides
     *
     * @param player player attacking
     * @param targetEntity target being attacked
     * @param stack ItemStack used to attack
     * @param hand hand used to attack
     */
    public void attackTargetEntityWithCurrentItem(PlayerEntity player, Entity targetEntity, IDualWieldedWeapon weapon, ItemStack stack, Hand hand) {
        //Forge hook (of course)
        //---------------------
        if (!ForgeHooks.onPlayerAttackTarget(player, targetEntity)) {
            return;
        }

        //Determine if entity should be attacked and all the attack's properties (damage, knockback, crit, ...)
        //-----------------------------------------------------------------------------------------------------
        if (targetEntity.canBeAttackedWithItem()) {
            if (!targetEntity.hitByEntity(player)) {
                //base damage
                float dmg = (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                //enchantment modifier
                float mdf_ench;
                if (targetEntity instanceof LivingEntity) {
                    mdf_ench = EnchantmentHelper.getModifierForCreature(stack, ((LivingEntity)targetEntity).getCreatureAttribute());
                } else {
                    mdf_ench = EnchantmentHelper.getModifierForCreature(stack, CreatureAttribute.UNDEFINED);
                }
                //cooldown modifier
                float mdf_cd = player.getCooledAttackStrength(0.5F);
                dmg = dmg * (0.2F + mdf_cd * mdf_cd * 0.8F);
                mdf_ench = mdf_ench * mdf_cd;
                player.resetCooldown();
                //critical hit & knockback
                if (dmg > 0.0F || mdf_ench > 0.0F) {
                    boolean crit = mdf_cd > 0.9F;
                    boolean sprint_crit = false;
                    int knockBack = 0;
                    knockBack = knockBack + EnchantmentHelper.getKnockbackModifier(player);
                    if (player.isSprinting() && crit) {
                        player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
                        ++knockBack;
                        sprint_crit = true;
                    }
                    boolean doCrit = crit
                            && player.fallDistance > 0.0F
                            && !player.isOnGround() &&
                            !player.isOnLadder() &&
                            !player.isInWater() &&
                            !player.isPotionActive(Effects.BLINDNESS)
                            && !player.isPassenger()
                            && (targetEntity instanceof LivingEntity);
                    doCrit = doCrit && !player.isSprinting();
                    if (doCrit) {
                        dmg *= 1.5F;
                    }
                    dmg = dmg + mdf_ench;
                    //Area of effect knockback
                    boolean aoe = false;
                    double d0 = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                    if (crit && !doCrit && !sprint_crit && player.isOnGround() && d0 < (double) player.getAIMoveSpeed()) {
                        aoe = true;
                    }
                    //Fire aspect
                    float targetHealthBefore = 0.0F;
                    boolean setFire = false;
                    int fireAspect = EnchantmentHelper.getFireAspectModifier(player);

                    if (targetEntity instanceof LivingEntity) {
                        targetHealthBefore = ((LivingEntity)targetEntity).getHealth();
                        if (fireAspect > 0 && !targetEntity.isBurning()) {
                            setFire = true;
                            targetEntity.setFire(1);
                        }
                    }
                    Vector3d v = targetEntity.getMotion();
                    boolean doDamage = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(player), dmg);

                    //Apply the damage and effects
                    //----------------------------
                    if (doDamage) {
                        //apply knockback
                        if (knockBack > 0) {
                            if (targetEntity instanceof LivingEntity) {
                                ((LivingEntity)targetEntity).applyKnockback(
                                        (float) knockBack * 0.5F,
                                        (double) MathHelper.sin(player.rotationYaw * 0.017453292F),
                                        (double)(-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                            } else {
                                targetEntity.addVelocity(
                                        (double)(-MathHelper.sin(player.rotationYaw * 0.017453292F) * (float)knockBack * 0.5F),
                                        0.1D,
                                        (double)(MathHelper.cos(player.rotationYaw * 0.017453292F) * (float)knockBack * 0.5F));
                            }
                            player.setMotion(v.mul(0.6, 1.0, 0.6));
                            player.setSprinting(false);
                        }
                        //apply aoe
                        if (aoe) {
                            player.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().expand(1.0D, 0.25D, 1.0D)).stream()
                                    .filter(e -> e != player
                                            && e != targetEntity
                                            && !player.isOnSameTeam(e)
                                            && player.getDistanceSq(e) < 9.0D)
                                    .forEach(e -> {
                                        e.applyKnockback(0.4F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                                        e.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0F);
                                    });
                            player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
                            player.spawnSweepParticles();
                        }
                        if (targetEntity instanceof ServerPlayerEntity && targetEntity.velocityChanged) {
                            ((ServerPlayerEntity)targetEntity).connection.sendPacket(new SEntityVelocityPacket(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.setMotion(v);
                        }
                        //play sounds
                        if (doCrit) {
                            player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
                            player.onCriticalHit(targetEntity);
                        }
                        if (!doCrit && !aoe) {
                            if (crit) {
                                player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                            else {
                                player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }
                        if (mdf_ench > 0.0F) {
                            player.onEnchantmentCritical(targetEntity);
                        }
                        //Effectiveness versus shields
                        if (!player.getEntityWorld().isRemote && targetEntity instanceof PlayerEntity && weapon.isEffectiveAgainstShield()) {
                            PlayerEntity targetPlayer = (PlayerEntity)targetEntity;
                            ItemStack targetHeldItem = targetPlayer.isHandActive() ? targetPlayer.getActiveItemStack() : null;
                            if (targetHeldItem != null && targetHeldItem.getItem() == Items.SHIELD) {
                                float breakBlockChance = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(player) * 0.05F;
                                if (sprint_crit) {
                                    breakBlockChance += 0.75F;
                                }
                                if (player.getEntityWorld().rand.nextFloat() < breakBlockChance) {
                                    targetPlayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
                                    player.getEntityWorld().setEntityState(targetPlayer, (byte)30);
                                }
                            }
                        }
                        //Don't forget this...
                        player.setLastAttackedEntity(targetEntity);
                        //Thorns enchantment (I'm starting to hate this method more and more...)
                        if (targetEntity instanceof LivingEntity) {
                            EnchantmentHelper.applyThornEnchantments((LivingEntity) targetEntity, player);
                        }
                        //Bane of arthropods
                        EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);
                        //Ender dragon logic... really?
                        Entity target = targetEntity;
                        if (targetEntity instanceof EnderDragonPartEntity) {
                            EnderDragonEntity dragon = ((EnderDragonPartEntity)targetEntity).dragon;
                            if (dragon instanceof LivingEntity) {
                                target = dragon;
                            }
                        }

                        //Set stack to null if stack size becomes zero
                        if (target instanceof LivingEntity) {
                            stack.hitEntity((LivingEntity) target, player);
                            if (stack.getCount() <= 0) {
                                player.setHeldItem(hand, ItemStack.EMPTY);
                                ForgeEventFactory.onPlayerDestroyItem(player, stack, Hand.MAIN_HAND);
                            }
                        }
                        //Stat tracking
                        if (targetEntity instanceof LivingEntity) {
                            float damageDone = targetHealthBefore - ((LivingEntity)targetEntity).getHealth();
                            player.addStat(Stats.DAMAGE_DEALT, Math.round(damageDone * 10.0F));
                            if (fireAspect > 0) {
                                targetEntity.setFire(fireAspect * 4);
                            }
                            if (player.getEntityWorld() instanceof ServerWorld && damageDone > 2.0F) {
                                int k = (int)((double)damageDone * 0.5D);
                                ((ServerWorld) player.getEntityWorld()).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, targetEntity.getPosX(), targetEntity.getPosY() + (double)(targetEntity.getHeight() * 0.5F), targetEntity.getPosZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }
                        //Add exhaustion
                        player.addExhaustion(0.3F);
                    } else {
                        player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);
                        if (setFire) {
                            targetEntity.extinguish();
                        }
                    }
                }
            }
        }
    }
}
