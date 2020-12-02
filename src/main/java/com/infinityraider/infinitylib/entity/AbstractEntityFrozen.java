package com.infinityraider.infinitylib.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * An invulnerable, invisible, stationary entity which can be used to store data to a point in the world
 * This does not take up a Block or a TileEntity space in the world, and can be placed inside blocks
 */
@SuppressWarnings("unused")
public abstract class AbstractEntityFrozen extends Entity implements IEntityAdditionalSpawnData {
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    private final ITextComponent name;

    public AbstractEntityFrozen(EntityType<?> type, World world, double x, double y, double z) {
        super(type, world);
        this.name = new StringTextComponent(this.name());
        this.firstUpdate = false;
        super.setRawPosition(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.lastTickPosX = x;
        this.lastTickPosY = y;
        this.lastTickPosZ = z;
        this.rotationYaw = 0;
        this.rotationPitch = 0;
        this.prevRotationYaw = 0;
        this.prevRotationPitch = 0;
    }

    protected abstract String name();

    protected abstract void update();

    protected abstract void onEntitySpawned();

    protected abstract void readDataFromNBT(CompoundNBT tag);

    protected abstract void writeDataToNBT(CompoundNBT tag);

    @Override
    public final void writeSpawnData(PacketBuffer buffer) {
        CompoundNBT tag = new CompoundNBT();
        this.writeDataToNBT(tag);
        buffer.writeCompoundTag(tag);
        this.onEntitySpawned();
    }

    @Override
    public final void readSpawnData(PacketBuffer data) {
        CompoundNBT tag = data.readCompoundTag();
        this.readDataFromNBT(tag);
        this.onEntitySpawned();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final boolean func_242278_a(BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final int getTeamColor() {
        return 16777215;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected final void preparePlayerToSpawn() {}

    @Override
    public final void setPose(Pose poseIn) {}

    @Override
    public final Pose getPose() {
        return Pose.STANDING;
    }

    @Override
    protected final void setRotation(float yaw, float pitch) { }

    @Override
    public final void setPosition(double x, double y, double z) {}

    @Override
    protected final void recenterBoundingBox() {}

    @OnlyIn(Dist.CLIENT)
    public final void rotateTowards(double yaw, double pitch) {}

    @Override
    public final void tick() {
        this.update();
    }

    @Override
    public final void baseTick() {}

    @Override
    protected final void decrementTimeUntilPortal() {}

    @Override
    public final int getMaxInPortalTime() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected final void setOnFireFromLava() {}

    @Override
    public void setFire(int seconds) {}

    @Override
    public void forceFireTicks(int ticks) {}

    @Override
    public final void extinguish() {}

    @Override
    protected final void outOfWorld() {}

    @Override
    public final boolean isOffsetPositionInLiquid(double x, double y, double z) {
        return false;
    }

    private boolean isLiquidPresentInAABB(AxisAlignedBB bb) {
        return false;
    }

    @Override
    public final void move(MoverType typeIn, Vector3d pos) {}

    @Override
    protected final Vector3d maybeBackOffFromEdge(Vector3d vec, MoverType mover) {
        return Vector3d.ZERO;
    }

    @Override
    protected final Vector3d handlePistonMovement(Vector3d pos) {
        return Vector3d.ZERO;
    }

    @Override
    protected final void doBlockCollisions() {}

    @Override
    public final void resetPositionToBB() {}

    @Override
    protected final void playStepSound(BlockPos pos, BlockState block) {}

    @Override
    protected final void playSwimSound(float volume) {}

    @Override
    protected final boolean makeFlySound() {
        return false;
    }

    @Override
    public final void playSound(SoundEvent soundIn, float volume, float pitch) {}

    @Override
    public final boolean isSilent() {
        return true;
    }

    @Override
    public final void setSilent(boolean isSilent) {}

    @Override
    public final boolean hasNoGravity() {
        return true;
    }

    @Override
    public final void setNoGravity(boolean noGravity) {}

    @Override
    protected final boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected final void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {}

    @Override
    public final boolean isImmuneToFire() {
        return true;
    }

    @Override
    public final boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    public final boolean canSwim() {
        return false;
    }

    @Override
    public final void updateSwimming() {}

    @Override
    protected final void doWaterSplashEffect() {}

    @Override
    public final boolean shouldSpawnRunningEffects() {
        return false;
    }

    @Override
    protected final void handleRunningEffect() {}

    @Override
    public final boolean isInLava() {
        return false;
    }

    @Override
    public final void moveRelative(float velocity, Vector3d direction) {}

    @Override
    public final void setWorld(World world) {}

    @Override
    public final void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {}

    @Override
    public final void func_242281_f(double p_242281_1_, double p_242281_3_, double p_242281_5_) {}

    @Override
    public final void moveForced(Vector3d vec) {}

    @Override
    public final void moveForced(double x, double y, double z) {}

    @Override
    public final void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn) {}

    @Override
    public final void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {}

    @Override
    public final void forceSetPosition(double x, double y, double z) {}

    @Override
    public final void onCollideWithPlayer(PlayerEntity entityIn) { }

    @Override
    public final void applyEntityCollision(Entity entityIn) {}

    @Override
    public final void addVelocity(double x, double y, double z) {}

    @Override
    protected final void markVelocityChanged() {}

    @Override
    public final boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public final float getPitch(float partialTicks) {
        return 0;
    }
    @Override
    public final float getYaw(float partialTicks) {
        return 0;
    }

    @Override
    public final boolean isEntityInsideOpaqueBlock() {
        return false;
    }

    @Override
    public final boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public final boolean canBePushed() {
        return false;
    }

    @Override
    public final boolean canCollide(Entity entity) {
        return false;
    }

    @Override
    public final boolean func_241845_aY() {
        return false;
    }

    @Override
    public final void updateRidden() {}

    @Override
    public final void updatePassenger(Entity entity) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public final void applyOrientationToEntity(Entity entityToUpdate) {}

    @Override
    public final double getMountedYOffset() {
        return 0;
    }

    @Override
    public final boolean startRiding(Entity entityIn) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final boolean isLiving() {
        return false;
    }

    @Override
    public final boolean startRiding(Entity entityIn, boolean force) {
        return false;
    }

    @Override
    protected final boolean canBeRidden(Entity entityIn) {
        return false;
    }

    @Override
    protected final boolean isPoseClear(Pose pose) {
        return true;
    }

    @Override
    public final void removePassengers() {}

    @Override
    public final void dismount() {}

    @Override
    public final void stopRiding() {}

    @Override
    protected final void addPassenger(Entity passenger) {}

    @Override
    protected final void removePassenger(Entity passenger) { }

    @Override
    protected final boolean canFitPassenger(Entity passenger) {
        return false;
    }

    @Override
    public final float getCollisionBorderSize() {
        return 0;
    }

    @Override
    public final void setPortal(BlockPos pos) {}

    @Override
    public final int getPortalCooldown() {
        return Integer.MAX_VALUE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final void setVelocity(double x, double y, double z) {}

    @Override
    public final boolean isBurning() {
        return false;
    }

    @Override
    public final boolean isPassenger() {
        return false;
    }

    @Override
    public final boolean isBeingRidden() {
        return false;
    }

    @Override
    public final boolean canBeRiddenInWater() {
        return false;
    }

    @Override
    public final boolean canBeRiddenInWater(Entity rider) {
        return false;
    }

    @Override
    public final void setSneaking(boolean keyDownIn) {}

    @Override
    public final boolean isSneaking() {
        return false;
    }

    @Override
    public final boolean isSteppingCarefully() {
        return false;
    }

    @Override
    public final boolean isSuppressingBounce() {
        return false;
    }

    @Override
    public final boolean isDiscrete() {
        return false;
    }

    @Override
    public final boolean isDescending() {
        return false;
    }

    @Override
    public final boolean isCrouching() {
        return false;
    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public void setSprinting(boolean sprinting) {}

    @Override
    public boolean isSwimming() {
        return false;
    }

    @Override
    public boolean isActualySwimming() {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isVisuallySwimming() {
        return false;
    }

    @Override
    public void setSwimming(boolean swimming) {}

    @Override
    public final boolean isInvisible() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final boolean isInvisibleToPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public final void setInvisible(boolean invisible) {}

    @Override
    protected final boolean getFlag(int flag) {
        return false;
    }

    @Override
    protected final void setFlag(int flag, boolean set) {}

    @Override
    public final int getAir() {
        return 0;
    }

    @Override
    public final void setAir(int air) {}

    @Override
    // onEntityStruckByLightning
    public final void func_241841_a(ServerWorld world, LightningBoltEntity lightningBolt) {}

    @Override
    public final void onEnterBubbleColumnWithAirAbove(boolean downwards) {}

    @Override
    public void onEnterBubbleColumn(boolean downwards) {}

    @Override
    public void func_241847_a(ServerWorld world, LivingEntity entity) {}

    @Override
    protected final void pushOutOfBlocks(double x, double y, double z) {}

    @Override
    public final void setMotionMultiplier(BlockState state, Vector3d motionMultiplierIn) {}

    @Override
    public final ITextComponent getName() {
        return this.name;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    public final boolean hitByEntity(Entity entityIn) {
        return true;
    }

    @Override
    public final boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public final boolean isInvulnerable() {
        return true;
    }

    @Override
    public final void setInvulnerable(boolean isInvulnerable) {}

    @Override
    public final void copyLocationAndAnglesFrom(Entity entity) {}

    @Override
    public final Entity changeDimension(ServerWorld server) {
        return null;
    }

    @Override
    @Nullable
    public Entity changeDimension(ServerWorld server, net.minecraftforge.common.util.ITeleporter teleporter) {
        return null;
    }

    @Override
    public final boolean canExplosionDestroyBlock(Explosion explosion, IBlockReader world, BlockPos pos, BlockState blockState, float explosionPower) {
        return false;
    }

    @Override
    public final int getMaxFallHeight() {
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final boolean canRenderOnFire() {
        return false;
    }

    @Override
    public final boolean isPushedByWater() {
        return false;
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {}

    @Override
    @Nullable
    public final ITextComponent getCustomName() {
        return null;
    }

    @Override
    public final boolean hasCustomName() {
        return false;
    }

    @Override
    public final void setCustomNameVisible(boolean alwaysRenderNameTag) {}

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public final void setPositionAndUpdate(double x, double y, double z) {}

    @Override
    public void recalculateSize() {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public final boolean getAlwaysRenderNameTagForRender() {
        return false;
    }

    public AxisAlignedBB getBoundingBox() {
        return ZERO_AABB;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return ZERO_AABB;
    }

    @Override
    protected AxisAlignedBB getBoundingBox(Pose pose) {
        return ZERO_AABB;
    }

    @Override
    public final void setBoundingBox(AxisAlignedBB bb) {}

    @Override
    public final boolean isImmuneToExplosions() {
        return true;
    }

    @Override
    public final void applyEnchantments(LivingEntity livingEntity, Entity entityIn) {}

    @Override
    public final float getRotatedYaw(Rotation transformRotation) {
        return 0;
    }

    @Override
    public final float getMirroredYaw(Mirror transformMirror) {
        return 0;
    }

    @Override
    public final boolean ignoreItemEntityData() {
        return false;
    }

    @Override
    @Nullable
    public final Entity getControllingPassenger() {
        return null;
    }

    @Override
    public final List<Entity> getPassengers() {
        return Collections.emptyList();
    }

    @Override
    public final boolean isPassenger(Entity entity) {
        return false;
    }

    @Override
    public final boolean isPassenger(Class<? extends Entity> entityClazz) {
        return false;
    }

    @Override
    public final Collection<Entity> getRecursivePassengers() {
        return Collections.emptySet();
    }

    @Override
    public final Stream<Entity> getSelfAndPassengers() {
        return Stream.of(this);
    }

    @Override
    public final boolean isOnePlayerRiding() {
        return false;
    }

    @Override
    public final Entity getLowestRidingEntity() {
        return this;
    }

    @Override
    public final boolean isRidingSameEntity(Entity entityIn) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final boolean isRidingOrBeingRiddenBy(Entity entityIn) {
        return false;
    }

    @Override
    public final boolean canPassengerSteer() {
        return false;
    }

    @Override
    @Nullable
    public final Entity getRidingEntity() {
        return null;
    }

    @Override
    public final PushReaction getPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    protected final int getFireImmuneTicks() {
        return Integer.MAX_VALUE;
    }

    @Override
    public final void lookAt(EntityAnchorArgument.Type anchor, Vector3d target) {}

    @Override
    public final boolean handleFluidAcceleration(ITag<Fluid> fluidTag, double p_210500_2_) {
        return false;
    }

    @Override
    public final void setMotion(Vector3d motionIn) {}

    @Override
    public final void setMotion(double x, double y, double z) {}

    @Override
    public final void setRawPosition(double x, double y, double z) {}

    @Override
    public final boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return false;
    }

    @Override
    protected final void readAdditional(CompoundNBT tag) {
        readDataFromNBT(tag);
    }

    @Override
    protected final void writeAdditional(CompoundNBT tag) {
        writeDataToNBT(tag);
    }
}
