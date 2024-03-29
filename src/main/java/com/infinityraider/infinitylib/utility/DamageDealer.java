package com.infinityraider.infinitylib.utility;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public class DamageDealer {
    private final String name;
    private float damageMultiplier;

    private Function<LivingEntity, Component> deathMessenger;
    private IDamageCallbackPre damageCallbackPre;
    private IDamageCallbackPost damageCallbackPost;

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

    public void apply(Entity target, float amount) {
        this.apply(target, this.createDamage(), amount);
    }

    public void apply(Entity target, float amount, Vec3 dir) {
        this.apply(target, this.createDamage().setDirection(dir), amount);
    }

    public void apply(Entity target, Entity source, float amount) {
        this.apply(target, this.createDamage(source), amount);
    }

    public void apply(Entity target, Entity source, float amount, Vec3 dir) {
        this.apply(target, this.createDamage(source).setDirection(dir), amount);
    }

    public void apply(Entity target, Entity source, Entity cause, float amount) {
        this.apply(target, this.createDamage(source, cause), amount);
    }

    public void apply(Entity target, Entity source, Entity cause, float amount, Vec3 dir) {
        this.apply(target, this.createDamage(source, cause).setDirection(dir), amount);
    }

    protected void apply(Entity target, InfinityDamageSource dmg, float amount) {
        //apply multiplier
        amount = amount * this.damageMultiplier;
        //pre damage callback
        if(this.damageCallbackPre != null) {
            amount = this.damageCallbackPre.preDamage(target, dmg, amount);
        }
        //apply damage
        dmg.apply(target, amount);
        //post damage callback
        if(this.damageCallbackPost != null) {
            this.damageCallbackPost.postDamage(target, dmg, amount);
        }
    }

    public String getName() {
        return this.name;
    }

    public DamageDealer setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
        return this;
    }

    public DamageDealer setDamageCallback(IDamageCallbackPre damageCallback) {
        this.damageCallbackPre = damageCallback;
        return this;
    }

    public DamageDealer setDamageCallback(IDamageCallbackPost damageCallback) {
        this.damageCallbackPost = damageCallback;
        return this;
    }

    public DamageDealer setDeathMessenger(Function<LivingEntity, Component> deathMessenger) {
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

    protected InfinityDamageSource createDamage(Entity source) {
        if(source instanceof Projectile) {
            return this.createDamage(source, (((Projectile) source).getOwner()));
        } else {
            return this.createDamage(source, source);
        }
    }

    protected InfinityDamageSource createDamage(Entity source, Entity cause) {
        InfinityDamageSource dmg = this.createDamage();
        return dmg.setSource(source).setCause(cause);
    }

    protected InfinityDamageSource createDamage() {
        return applySettings(new InfinityDamageSource(this.getName()));
    }

    protected InfinityDamageSource applySettings(InfinityDamageSource dmg) {
        if(this.deathMessenger != null) {
            dmg.setDeathMessenger(this.deathMessenger);
        }
        if (this.projectile) {
            dmg.setProjectile();
        }
        if (this.explosion) {
            dmg.setExplosion();
        }
        if (this.bypassArmor) {
            dmg.bypassArmor();
        }
        if (this.hurtCreative) {
            dmg.bypassInvul();
        }
        if (this.absolute) {
            dmg.bypassMagic();
        }
        if (this.scalable) {
            dmg.setScalesWithDifficulty();
        }
        if (this.fireDamage) {
            dmg.setIsFire();
        }
        if (this.magicDamage) {
            dmg.setMagic();
        }
        return dmg;
    }

    public static class InfinityDamageSource extends DamageSource {
        private Entity source;
        private Entity cause;
        private Vec3 direction;
        private Function<LivingEntity, Component> deathMessenger;

        protected InfinityDamageSource(String damageTypeIn) {
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

        public InfinityDamageSource setDirection(Vec3 direction) {
            this.direction = direction;
            return this;
        }

        public InfinityDamageSource setDeathMessenger(Function<LivingEntity, Component> deathMessenger) {
            this.deathMessenger = deathMessenger;
            return this;
        }

        @Override
        @Nullable
        public Entity getEntity() {
            return this.cause == null ? super.getEntity() : this.cause;
        }

        @Override
        @Nullable
        public Entity getDirectEntity() {
            return this.source == null ? super.getDirectEntity() : this.source;
        }

        public Vec3 getDirection() {
            return this.direction;
        }

        @Nonnull
        @Override
        public Component getLocalizedDeathMessage(@Nonnull LivingEntity target) {
            return this.deathMessenger == null ? super.getLocalizedDeathMessage(target) : deathMessenger.apply(target);
        }

        public void apply(Entity target, float amount) {
            if(amount > 0) {
                target.hurt(this, amount);
            }
        }
    }

    @FunctionalInterface
    public interface IDamageCallbackPre {
        float preDamage(Entity target, InfinityDamageSource dmg, float amount);
    }

    @FunctionalInterface
    public interface IDamageCallbackPost {
        void postDamage(Entity target, InfinityDamageSource dmg, float amount);
    }
}