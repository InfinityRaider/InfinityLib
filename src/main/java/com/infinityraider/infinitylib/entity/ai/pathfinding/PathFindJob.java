package com.infinityraider.infinitylib.entity.ai.pathfinding;

import com.google.common.collect.Lists;
import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Objects;

public class PathFindJob extends Path {
    public static final double DEFAULT_SPEED = 1.5D;

    private final Mob entity;
    private final double speed;
    private final PathCalculator.ITarget target;
    private final PathCalculator.IPathOptions options;
    private final ICallback callback;

    private boolean cancelled;

    public PathFindJob(Mob entity, PathCalculator.ITarget target, PathCalculator.IPathOptions options) {
        this(entity, DEFAULT_SPEED, target, options);
    }

    public PathFindJob(Mob entity, double speed, PathCalculator.ITarget target, PathCalculator.IPathOptions options) {
        this(entity, speed, target, options, ICallback.none);
    }

    public PathFindJob(Mob entity, double speed, PathCalculator.ITarget target, PathCalculator.IPathOptions options, ICallback callback) {
        super(Lists.newArrayList(target.getTargetPoint()), target.getTargetPoint().asBlockPos(), false);
        this.entity = Objects.requireNonNull(entity);
        this.speed = speed;
        this.target = Objects.requireNonNull(target);
        this.options = options;
        this.callback = Objects.requireNonNull(callback);
        this.cancelled = false;
    }


    //Getters
    //-------

    public Mob entity() {
        return this.entity;
    }

    public double speed() {
        return this.speed;
    }

    public PathCalculator.IPathOptions options() {
        return this.options;
    }

    public Node getEntityPoint() {
        Vec3 entityPos = this.getEntityPosition();
        return new Node(Mth.floor(entityPos.x), Mth.floor(entityPos.y), Mth.floor(entityPos.z));
    }

    public Vec3 getEntityPosition() {
        return this.entity().position().add(new Vec3(
                (double)((int)(this.entity().getBbWidth() + 1.0F)) * 0.5D,
                0,
                (double)((int)(this.entity().getBbHeight() + 1.0F)) * 0.5D
        ));
    }


    //Job methods
    //-----------

    public boolean isValid() {
        return (!this.cancelled) && this.entity().isAlive() && this.target().isValid();
    }

    public PathFindJob cancel() {
        this.cancelled = true;
        this.entity().getNavigation().moveTo((Path) null, this.speed());
        InfinityLib.instance.queueTask(this.callback::onJobCancelled);
        return this;
    }

    public PathFindJob finish(Path path) {
        this.entity().getNavigation().moveTo(path, this.speed());
        InfinityLib.instance.queueTask(() -> this.callback.onJobFinished(this.entity(), path));
        this.cancelled = true;
        return this;
    }

    public PathFindJob fail() {
        this.cancelled = true;
        this.entity().getNavigation().moveTo((Path) null, this.speed());
        InfinityLib.instance.queueTask(this.callback::onJobFailed);
        return this;
    }

    public interface ICallback {
        void onJobCancelled();

        void onJobFinished(Mob entity, Path path);

        void onJobFailed();

        ICallback none = new ICallback() {
            @Override
            public void onJobCancelled() {}

            @Override
            public void onJobFinished(Mob entity, Path path) {}

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

    public boolean hasTargetChanged(Vec3 previous) {
        return this.target.hasTargetChanged(previous);
    }

    public Vec3 getTargetVector() {
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
    public void advance() {}

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    @Nullable
    public Node getEndNode() {
        return this.target.getTargetPoint();
    }

    @Override
    public Node getNode(int index) {
        return this.getEntityPoint();
    }

    @Override
    public void replaceNode(int index, Node point) {}

    @Override
    public int getNodeCount() {
        return Mth.ceil(this.entity().position().distanceTo(this.target.getTarget()));
    }

    @Override
    public int getNextNodeIndex() {
        return 0;
    }

    @Override
    public void setNextNodeIndex(int currentPathIndexIn) {}

    @Override
    public Vec3 getEntityPosAtNode(Entity entity, int index) {
        return this.getEntityPosition();
    }

    @Override
    public Vec3 getNextEntityPos(Entity entity)  {
        return this.getEntityPosAtNode(entity, this.getNextNodeIndex());
    }

    @Override
    public boolean sameAs(Path other) {
        return other == this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Node[] getOpenSet() {
        return new Node[] {};
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Node[] getClosedSet() {
        return new Node[] {};
    }

    public BlockPos getTarget()  {
        return this.getEndNode().asBlockPos();
    }
}