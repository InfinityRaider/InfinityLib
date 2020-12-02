package com.infinityraider.infinitylib.entity.ai.pathfinding;

import com.google.common.collect.Lists;
import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Objects;

public class PathFindJob extends Path {
    public static final double DEFAULT_SPEED = 1.5D;

    private final MobEntity entity;
    private final double speed;
    private final PathCalculator.ITarget target;
    private final PathCalculator.IPathOptions options;
    private final ICallback callback;

    private boolean cancelled;

    public PathFindJob(MobEntity entity, PathCalculator.ITarget target, PathCalculator.IPathOptions options) {
        this(entity, DEFAULT_SPEED, target, options);
    }

    public PathFindJob(MobEntity entity, double speed, PathCalculator.ITarget target, PathCalculator.IPathOptions options) {
        this(entity, speed, target, options, ICallback.none);
    }

    public PathFindJob(MobEntity entity, double speed, PathCalculator.ITarget target, PathCalculator.IPathOptions options, ICallback callback) {
        super(Lists.newArrayList(target.getTargetPoint()), target.getTargetPoint().func_224759_a(), false);
        this.entity = Objects.requireNonNull(entity);
        this.speed = speed;
        this.target = Objects.requireNonNull(target);
        this.options = options;
        this.callback = Objects.requireNonNull(callback);
        this.cancelled = false;
    }


    //Getters
    //-------

    public MobEntity entity() {
        return this.entity;
    }

    public double speed() {
        return this.speed;
    }

    public PathCalculator.IPathOptions options() {
        return this.options;
    }

    public PathPoint getEntityPoint() {
        Vector3d entityPos = this.getEntityPosition();
        return new PathPoint(MathHelper.floor(entityPos.x), MathHelper.floor(entityPos.y), MathHelper.floor(entityPos.z));
    }

    public Vector3d getEntityPosition() {
        return this.entity().getPositionVec().add(new Vector3d(
                (double)((int)(this.entity().getWidth() + 1.0F)) * 0.5D,
                0,
                (double)((int)(this.entity().getWidth() + 1.0F)) * 0.5D
        ));
    }


    //Job methods
    //-----------

    public boolean isValid() {
        return (!this.cancelled) && this.entity().isAlive() && this.target().isValid();
    }

    public PathFindJob cancel() {
        this.cancelled = true;
        this.entity().getNavigator().setPath(null, this.speed());
        InfinityLib.instance.proxy().queueTask(this.callback::onJobCancelled);
        return this;
    }

    public PathFindJob finish(Path path) {
        this.entity().getNavigator().setPath(path, this.speed());
        InfinityLib.instance.proxy().queueTask(() -> this.callback.onJobFinished(this.entity(), path));
        this.cancelled = true;
        return this;
    }

    public PathFindJob fail() {
        this.cancelled = true;
        this.entity().getNavigator().setPath(null, this.speed());
        InfinityLib.instance.proxy().queueTask(this.callback::onJobFailed);
        return this;
    }

    public interface ICallback {
        void onJobCancelled();

        void onJobFinished(MobEntity entity, Path path);

        void onJobFailed();

        ICallback none = new ICallback() {
            @Override
            public void onJobCancelled() {}

            @Override
            public void onJobFinished(MobEntity entity, Path path) {}

            @Override
            public void onJobFailed() {}
        };
    }


    //Target properties
    //-----------------

    public PathCalculator.ITarget target() {
        return this.target;
    }

    public boolean canTargetMove() {
        return this.target.canTargetMove();
    }

    public boolean hasTargetChanged(Vector3d previous) {
        return this.target.hasTargetChanged(previous);
    }

    public Vector3d getTargetVector() {
        return this.target.getTarget();
    }


    //Path options
    //------------
    public boolean canOpenDoors() {
        return this.options().canOpenDoors();
    }

    public boolean canClimbLadders() {
        return this.options().canClimbLadders();
    }

    public boolean canClimbWalls() {
        return this.options().canClimbWalls();
    }

    public boolean canSwim() {
        return this.options().canSwim();
    }

    public boolean canFly() {
        return this.options().canFly();
    }

    public int maxFallHeight() {
        return this.options().maxFallHeight();
    }

    public int maxJumpHeight() {
        return this.options().maxJumpHeight();
    }


    //Overrides from Path
    //-------------------

    @Override
    public void incrementPathIndex() {}

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    @Nullable
    public PathPoint getFinalPathPoint() {
        return this.target.getTargetPoint();
    }

    @Override
    public PathPoint getPathPointFromIndex(int index) {
        return this.getEntityPoint();
    }

    @Override
    public void setPoint(int index, PathPoint point) {}

    @Override
    public int getCurrentPathLength() {
        return MathHelper.ceil(this.entity().getPositionVec().distanceTo(this.target.getTarget()));
    }

    @Override
    public void setCurrentPathLength(int length) {}

    @Override
    public int getCurrentPathIndex() {
        return 0;
    }

    @Override
    public void setCurrentPathIndex(int currentPathIndexIn) {}

    @Override
    public Vector3d getVectorFromIndex(Entity entity, int index) {
        return this.getEntityPosition();
    }

    @Override
    public Vector3d getPosition(Entity entity)  {
        return this.getVectorFromIndex(entity, this.getCurrentPathIndex());
    }

    @Override
    public boolean isSamePath(Path other) {
        return other == this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public PathPoint[] getOpenSet() {
        return new PathPoint[] {};
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public PathPoint[] getClosedSet() {
        return new PathPoint[] {};
    }

    public BlockPos getTarget()  {
        return this.getFinalPathPoint().func_224759_a();
    }
}