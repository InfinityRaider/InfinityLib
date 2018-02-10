package com.infinityraider.infinitylib.utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.function.Function;

public class DamageDealer {
    private final String name;
    private float damageMultiplier;

    private Function<EntityLivingBase, ITextComponent> deathMessenger;

    private boolean projectile;
    private boolean explosion;
    private boolean bypassArmor;
    private boolean hurtCreative;
    private boolean absolute;
    private boolean scalable;
    private boolean fireDamage;
    private boolean magicDamage;

    public DamageDealer(String name) {
        this(name, 1.0F);
    }

    public DamageDealer(String name, float damageMultiplier) {
        this.name = name;
        this.damageMultiplier = damageMultiplier;
    }

    public void applyDamage(EntityLivingBase target, float amount) {
        target.attackEntityFrom(this.createDamage(), amount * this.damageMultiplier);
    }

    public void applyDamage(EntityLivingBase target, Entity source, float amount) {
        target.attackEntityFrom(this.createDamage(source), amount * this.damageMultiplier);
    }

    public void applyDamage(EntityLivingBase target, Entity source, Entity cause, float amount) {
        target.attackEntityFrom(this.createDamage(source, cause), amount * this.damageMultiplier);
    }

    public String getName() {
        return this.name;
    }

    public DamageDealer setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
        return this;
    }

    public DamageDealer setDeathMessenger(Function<EntityLivingBase, ITextComponent> deathMessenger) {
        this.deathMessenger = deathMessenger;
        return this;
    }

    public DamageDealer setProjectile(boolean status) {
        this.projectile = status;
        return this;
    }

    public DamageDealer setExplosion(boolean status) {
        this.explosion = status;
        return this;
    }

    public DamageDealer setBypassArmor(boolean status) {
        this.bypassArmor = status;
        return this;
    }

    public DamageDealer setHurtCreative(boolean status) {
        this.hurtCreative = status;
        return this;
    }

    public DamageDealer setAbsolute(boolean status) {
        this.absolute = status;
        return this;
    }

    public DamageDealer setScalable(boolean status) {
        this.scalable = status;
        return this;
    }

    public DamageDealer setFireDamage(boolean status) {
        this.fireDamage = status;
        return this;
    }

    public DamageDealer setMagicDamage(boolean status) {
        this.magicDamage = status;
        return this;
    }

    public InfinityDamageSource createDamage(Entity source) {
        if(source instanceof EntityThrowable) {
            return this.createDamage(source, (((EntityThrowable) source).getThrower()));
        } else {
            return this.createDamage(source, source);
        }
    }

    public InfinityDamageSource createDamage(Entity source, Entity cause) {
        InfinityDamageSource dmg = this.createDamage();
        return dmg.setSource(source).setCause(cause);
    }

    public InfinityDamageSource createDamage() {
        InfinityDamageSource dmg = new InfinityDamageSource(this.getName()).setDeathMessager(this.deathMessenger);
        if(this.projectile) {
            dmg.setProjectile();
        }
        if(this.explosion) {
            dmg.setExplosion();
        }
        if(this.bypassArmor) {
            dmg.setDamageBypassesArmor();
        }
        if(this.hurtCreative) {
            dmg.setDamageAllowedInCreativeMode();
        }
        if(this.absolute) {
            dmg.setDamageIsAbsolute();
        }
        if(this.scalable) {
            dmg.setDifficultyScaled();
        }
        if(this.fireDamage) {
            dmg.setFireDamage();
        }
        if(this.magicDamage) {
            dmg.setMagicDamage();
        }
        return dmg;
    }

    private static class InfinityDamageSource extends DamageSource {
        private Entity source;
        private Entity cause;
        private Function<EntityLivingBase, ITextComponent> deathMessenger;

        public InfinityDamageSource(String damageTypeIn) {
            super(damageTypeIn);
        }

        public InfinityDamageSource setSource(Entity source) {
            this.source = source;
            return this;
        }

        public InfinityDamageSource setCause(Entity cause) {
            this.cause = cause;
            return this;
        }

        public InfinityDamageSource setDeathMessager(Function<EntityLivingBase, ITextComponent> deathMessenger) {
            this.deathMessenger = deathMessenger;
            return this;
        }

        @Override
        @Nullable
        public Entity getSourceOfDamage() {
            return this.source == null ? super.getSourceOfDamage() : this.source;
        }

        @Override
        @Nullable
        public Entity getEntity() {
            return this.cause == null ? super.getEntity() : this.cause;
        }

        @Override
        public ITextComponent getDeathMessage(EntityLivingBase target) {
            return this.deathMessenger == null ? super.getDeathMessage(target) : deathMessenger.apply(target);
        }
    }
}