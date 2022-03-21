package com.infinityraider.infinitylib.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.BlockUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListenerRegistrar;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An invulnerable, invisible, stationary entity which can be used to store data to a point in the world
 * This does not take up a Block or a TileEntity space in the world, and can be placed inside blocks
 */
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractEntityFrozen extends Entity implements IEntityAdditionalSpawnData {
    private static final AABB ZERO_AABB = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    private final TextComponent name;

    private Vec3 position;
    private BlockPos blockPos;
    private ChunkPos chunkPos;

    public AbstractEntityFrozen(EntityType<?> type, Level world, double x, double y, double z) {
        super(type, world);
        this.name = new TextComponent(this.name());
        this.position = new Vec3(x, y, z);
        this.blockPos = new BlockPos(this.position).immutable();
        this.chunkPos = new ChunkPos(this.blockPos);
        this.firstTick = false;
        this.noPhysics = true;
        this.xOld = x;
        this.yOld = y;
        this.zOld = z;
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setYRot(0);
        this.setXRot(0);
        this.yRotO = 0;
        this.xRotO = 0;
    }

    protected abstract String name();

    protected abstract void update();

    protected abstract void onEntitySpawned();

    protected abstract void readDataFromNBT(CompoundTag tag);

    protected abstract void writeDataToNBT(CompoundTag tag);

    @Override
    public Vec3 position() {
        return this.position;
    }

    @Override
    public BlockPos blockPosition() {
        return this.blockPos;
    }

    @Override
    public double getX(double pScale) {
        return this.position().x;
    }

    @Override
    public double getY(double pScale) {
        return this.position().y;
    }

    @Override
    public double getEyeY() {
        return this.position().y;
    }

    @Override
    public double getZ(double pScale) {
        return this.position().z;
    }

    @Override
    public BlockPos eyeBlockPosition() {
        return this.blockPosition();
    }

    @Override
    public ChunkPos chunkPosition() {
        return this.chunkPos;
    }

    @Override
    public final void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        this.writeDataToNBT(tag);
        buffer.writeNbt(tag);
        this.onEntitySpawned();
    }

    @Override
    public final void readSpawnData(FriendlyByteBuf data) {
        CompoundTag tag = data.readNbt();
        this.readDataFromNBT(tag);
        this.onEntitySpawned();
    }

    @Override
    protected final void readAdditionalSaveData(CompoundTag tag) {
        readDataFromNBT(tag);
    }

    @Override
    protected final void addAdditionalSaveData(CompoundTag tag) {
        writeDataToNBT(tag);
    }

    @Override
    public boolean isColliding(BlockPos pPos, BlockState pState) {
        return false;
    }

    @Override
    public int getTeamColor() {
        return 16777215;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public void setPose(Pose pPose) {}

    @Override
    protected void setRot(float yaw, float pitch) {}

    @Override
    public void setPos(double x, double y, double z) {}

    @Override
    protected void reapplyPosition() { }

    @Override
    public void turn(double pYaw, double pPitch) {}

    @Override
    public void setSharedFlagOnFire(boolean pIsOnFire) {}

    @Override
    public void setPortalCooldown() {}

    @Override
    public boolean isOnPortalCooldown() {
        return true;
    }

    @Override
    protected void processPortalCooldown() {}

    @Override
    public int getPortalWaitTime() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void lavaHurt() {}

    @Override
    public void setSecondsOnFire(int pSeconds) {}

    @Override
    public void setRemainingFireTicks(int pTicks) {}

    @Override
    public int getRemainingFireTicks() {
        return 0;
    }

    @Override
    public void clearFire() {}

    @Override
    protected void outOfWorld() {}

    @Override
    public void setOnGround(boolean pGrounded) {}

    @Override
    public boolean isOnGround() {
        return false;
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {}

    @Override
    protected boolean isHorizontalCollisionMinor(Vec3 vec) {
        return false;
    }

    @Override
    protected void tryCheckInsideBlocks() {}

    @Override
    protected void playEntityOnFireExtinguishedSound() {}

    @Override
    protected void processFlappingMovement() {}

    @Override
    protected float getBlockJumpFactor() {
        return 0;
    }

    @Override
    protected float getBlockSpeedFactor() {
        return 0;
    }

    @Override
    protected BlockPos getBlockPosBelowThatAffectsMyMovement() {
        return super.getBlockPosBelowThatAffectsMyMovement();
    }

    @Override
    protected Vec3 maybeBackOffFromEdge(Vec3 pVec, MoverType pMover) {
        return pVec;
    }

    @Override
    protected Vec3 limitPistonMovement(Vec3 pPos) {
        return pPos;
    }

    @Override
    protected float nextStep() {
        return 0;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return super.getSwimSound();
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return super.getSwimSound();
    }

    @Override
    protected SoundEvent getSwimHighSpeedSplashSound() {
        return super.getSwimSound();
    }

    @Override
    protected void checkInsideBlocks() { }

    @Override
    protected void onInsideBlock(BlockState pState) {}

    @Override
    public void gameEvent(GameEvent pEvent, @Nullable Entity pEntity, BlockPos pPos) {}

    @Override
    public void gameEvent(GameEvent pEvent, @Nullable Entity pEntity) {}

    @Override
    public void gameEvent(GameEvent pEvent, BlockPos pPos) {}

    @Override
    public void gameEvent(GameEvent pEvent) {}

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {}

    @Override
    protected void playSwimSound(float pVolume) {}

    @Override
    protected void onFlap() {}

    @Override
    protected boolean isFlapping() {
        return false;
    }

    @Override
    public void playSound(SoundEvent pSound, float pVolume, float pPitch) {}

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public void setSilent(boolean pIsSilent) {}

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void setNoGravity(boolean pNoGravity) { }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean occludesVibrations() {
        return false;
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {}

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public boolean isInWater() {
        return super.isInWater();
    }

    @Override
    public boolean isInWaterOrRain() {
        return super.isInWaterOrRain();
    }

    @Override
    public boolean isInWaterRainOrBubble() {
        return super.isInWaterRainOrBubble();
    }

    @Override
    public boolean isInWaterOrBubble() {
        return super.isInWaterOrBubble();
    }

    @Override
    public boolean isUnderWater() {
        return super.isUnderWater();
    }

    @Override
    public void updateSwimming() {}

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Override
    protected void doWaterSplashEffect() {}

    @Override
    public boolean canSpawnSprintParticle() {
        return false;
    }

    @Override
    protected void spawnSprintParticle() {}

    @Override
    public void moveRelative(float pAmount, Vec3 pRelative) {}

    @Override
    public void absMoveTo(double pX, double pY, double pZ, float pYaw, float pPitch) {}

    @Override
    public void absMoveTo(double pX, double pY, double pZ) {}

    @Override
    public void moveTo(Vec3 pVec) { }

    @Override
    public void moveTo(double p_20105_, double p_20106_, double p_20107_) {}

    @Override
    public void moveTo(BlockPos pPos, float pRotationYaw, float pRotationPitch) { }

    @Override
    public void moveTo(double pX, double pY, double pZ, float pYaw, float pPitch) {}

    @Override
    public void playerTouch(Player pPlayer) {}

    @Override
    public void push(Entity pEntity) {}

    @Override
    public void push(double pX, double pY, double pZ) {}

    @Override
    protected void markHurt() {}

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public float getViewXRot(float pPartialTicks) {
        return 0;
    }

    @Override
    public float getViewYRot(float pPartialTicks) {
        return 0;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void awardKillScore(Entity pKilled, int pScoreValue, DamageSource pSource) {}

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return false;
    }

    @Override
    protected boolean repositionEntityAfterLoad() {
        return false;
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        return InteractionResult.FAIL;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public void rideTick() {}

    @Override
    public void positionRider(Entity pPassenger) {}

    @Override
    public void onPassengerTurned(Entity pEntityToUpdate) {}

    @Override
    public double getMyRidingOffset() {
        return 0;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0;
    }

    @Override
    public boolean startRiding(Entity pEntity) {
        return false;
    }

    @Override
    public boolean showVehicleHealth() {
        return false;
    }

    @Override
    public boolean startRiding(Entity pEntity, boolean pForce) {
        return false;
    }

    @Override
    protected boolean canRide(Entity pEntity) {
        return false;
    }

    @Override
    protected boolean canEnterPose(Pose pPose) {
        return false;
    }

    @Override
    public void ejectPassengers() {}

    @Override
    public void removeVehicle() {}

    @Override
    public void stopRiding() {}

    @Override
    protected void addPassenger(Entity pPassenger) {}

    @Override
    protected void removePassenger(Entity pPassenger) {}

    @Override
    protected boolean canAddPassenger(Entity pPassenger) {
        return false;
    }

    @Override
    public void lerpTo(double pX, double pY, double pZ, float pYaw, float pPitch, int pPosRotationIncrements, boolean pTeleport) {}

    @Override
    public void lerpHeadTo(float pYaw, int pPitch) {}

    @Override
    public float getPickRadius() {
        return 0;
    }

    @Override
    public Vec3 getLookAngle() {
        return Vec3.ZERO;
    }

    @Override
    public Vec3 getHandHoldingItemAngle(Item p_204035_) {
        return Vec3.ZERO;
    }

    @Override
    public Vec2 getRotationVector() {
        return Vec2.ZERO;
    }

    @Override
    public Vec3 getForward() {
        return Vec3.ZERO;
    }

    @Override
    public void handleInsidePortal(BlockPos pPos) {}

    @Override
    protected void handleNetherPortal() {}

    @Override
    public int getDimensionChangingDelay() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void lerpMotion(double pX, double pY, double pZ) {}

    @Override
    public void handleEntityEvent(byte pId) {}

    @Override
    public void animateHurt() {}

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return ImmutableSet.of();
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return ImmutableSet.of();
    }

    @Override
    public Iterable<ItemStack> getAllSlots() {
        return ImmutableSet.of();
    }

    @Override
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {}

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isPassenger() {
        return false;
    }

    @Override
    public boolean isVehicle() {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean rideableUnderWater() {
        return false;
    }

    @Override
    public void setShiftKeyDown(boolean pKeyDown) {}

    @Override
    public boolean isShiftKeyDown() {
        return false;
    }

    @Override
    public boolean isSteppingCarefully() {
        return false;
    }

    @Override
    public boolean isSuppressingBounce() {
        return true;
    }

    @Override
    public boolean isDiscrete() {
        return true;
    }

    @Override
    public boolean isDescending() {
        return false;
    }

    @Override
    public boolean isCrouching() {
        return false;
    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public void setSprinting(boolean pSprinting) {}

    @Override
    public boolean isSwimming() {
        return false;
    }

    @Override
    public boolean isVisuallySwimming() {
        return false;
    }

    @Override
    public boolean isVisuallyCrawling() {
        return false;
    }

    @Override
    public void setSwimming(boolean pSwimming) {}

    @Override
    public boolean isCurrentlyGlowing() {
        return false;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public boolean isInvisibleTo(Player pPlayer) {
        return true;
    }

    @Nullable
    @Override
    public GameEventListenerRegistrar getGameEventListenerRegistrar() {
        return null;
    }

    @Nullable
    @Override
    public Team getTeam() {
        return null;
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        return false;
    }

    @Override
    public boolean isAlliedTo(Team pTeam) {
        return false;
    }

    @Override
    public void setInvisible(boolean pInvisible) {}

    @Override
    public int getMaxAirSupply() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getAirSupply() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setAirSupply(int pAir) { }

    @Override
    public int getTicksFrozen() {
        return 0;
    }

    @Override
    public void setTicksFrozen(int pTicksFrozen) {}

    @Override
    public float getPercentFrozen() {
        return 0;
    }

    @Override
    public boolean isFullyFrozen() {
        return false;
    }

    @Override
    public int getTicksRequiredToFreeze() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void thunderHit(ServerLevel pLevel, LightningBolt pLightning) {}

    @Override
    public void onAboveBubbleCol(boolean pDownwards) {}

    @Override
    public void onInsideBubbleColumn(boolean pDownwards) {}

    @Override
    public void killed(ServerLevel pLevel, LivingEntity pKilledEntity) {}

    @Override
    public void resetFallDistance() {}

    @Override
    protected void moveTowardsClosestSpace(double pX, double pY, double pZ) {}

    @Override
    public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {}

    @Override
    public float getYHeadRot() {
        return 0;
    }

    @Override
    public void setYHeadRot(float pRotation) {}

    @Override
    public void setYBodyRot(float pOffset) {}

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean skipAttackInteraction(Entity pEntity) {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public void setInvulnerable(boolean pIsInvulnerable) {}

    @Override
    public void copyPosition(Entity pEntity) {}

    @Override
    public void restoreFrom(Entity pEntity) {}

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel pDestination) {
        return this;
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel pDestination, ITeleporter teleporter) {
        return this;
    }

    @Override
    protected void removeAfterChangingDimensions() {}

    @Nullable
    @Override
    protected PortalInfo findDimensionEntryPoint(ServerLevel pDestination) {
        return null;
    }

    @Override
    protected Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel pDestination, BlockPos pFindFrom, boolean pIsToNether, WorldBorder pWorldBorder) {
        return Optional.empty();
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public float getBlockExplosionResistance(Explosion pExplosion, BlockGetter pLevel, BlockPos pPos, BlockState pBlockState, FluidState pFluidState, float pExplosionPower) {
        return Float.MAX_VALUE;
    }

    @Override
    public boolean shouldBlockExplode(Explosion pExplosion, BlockGetter pLevel, BlockPos pPos, BlockState pBlockState, float pExplosionPower) {
        return false;
    }

    @Override
    public int getMaxFallDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public void setCustomNameVisible(boolean pAlwaysRenderNameTag) {}

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public void dismountTo(double pX, double pY, double pZ) {}

    @Override
    public void teleportTo(double pX, double pY, double pZ) {}

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    public void refreshDimensions() {}

    @Override
    public Direction getDirection() {
        return Direction.NORTH;
    }

    @Override
    public Direction getMotionDirection() {
        return Direction.NORTH;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return ZERO_AABB;
    }

    @Override
    protected AABB getBoundingBoxForPose(Pose pPose) {
        return ZERO_AABB;
    }

    @Override
    public InteractionResult interactAt(Player pPlayer, Vec3 pVec, InteractionHand pHand) {
        return InteractionResult.FAIL;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public void doEnchantDamageEffects(LivingEntity pLivingEntity, Entity pEntity) {}

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer) {}

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer) {}

    @Override
    public float rotate(Rotation pTransformRotation) {
        return 0;
    }

    @Override
    public float mirror(Mirror pTransformMirror) {
        return 0;
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return null;
    }

    @Nullable
    @Override
    public Entity getFirstPassenger() {
        return null;
    }

    @Override
    public boolean hasPassenger(Entity pEntity) {
        return false;
    }

    @Override
    public boolean hasPassenger(Predicate<Entity> pPredicate) {
        return false;
    }

    @Override
    public Stream<Entity> getSelfAndPassengers() {
        return Stream.of(this);
    }

    @Override
    public Stream<Entity> getPassengersAndSelf() {
        return Stream.of(this);
    }

    @Override
    public Iterable<Entity> getIndirectPassengers() {
        return ImmutableSet.of();
    }

    @Override
    public boolean hasExactlyOnePlayerPassenger() {
        return false;
    }

    @Override
    public Entity getRootVehicle() {
        return this;
    }

    @Override
    public boolean isPassengerOfSameVehicle(Entity pEntity) {
        return false;
    }

    @Override
    public boolean hasIndirectPassenger(Entity pEntity) {
        return false;
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return false;
    }

    @Nullable
    @Override
    public Entity getVehicle() {
        return null;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.AMBIENT;
    }

    @Override
    protected int getFireImmuneTicks() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void lookAt(EntityAnchorArgument.Anchor pAnchor, Vec3 pTarget) {}

    @Override
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> pFluidTag, double pMotionScale) {
        return false;
    }

    @Override
    public Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public void setDeltaMovement(Vec3 pMotion) {}

    @Override
    public void setDeltaMovement(double pX, double pY, double pZ) {}

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public boolean isFreezing() {
        return false;
    }

    @Override
    public float getYRot() {
        return 0;
    }

    @Override
    public void setYRot(float pYRot) {}

    @Override
    public float getXRot() {
        return 0;
    }

    @Override
    public void setXRot(float pXRot) {}

    @Override
    public boolean mayInteract(Level pLevel, BlockPos pPos) {
        return false;
    }

    @Override
    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return false;
    }
}
