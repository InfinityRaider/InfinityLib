package com.infinityraider.infinitylib.utility;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class RayTraceHelper {
    public static Optional<HitResult> getTargetBlock(Entity entity, double distance) {
        return getRayFromEyesAndDistance(entity, distance).flatMap(ray ->
                rayTraceBlockForEntity(entity, entity.getLevel(), ray, false, false));
    }

    public static Optional<EntityHitResult> getTargetEntity(Entity entity, double distance) {
        return getTargetEntity(entity, distance, Entity.class);
    }

    @SuppressWarnings("Unchecked")
    public static Optional<EntityHitResult> getTargetEntity(Entity entity, double distance, Class<? extends Entity> entityClass) {
        return getTargetEntity(entity, distance, new PredicateInstanceOf(entityClass));
    }

    public static Optional<EntityHitResult> getTargetEntity(Entity entity, double distance, Predicate<? super Entity> filter) {
        return getRayFromEyesAndDistance(entity, distance).flatMap(ray ->
                rayTraceEntityForEntity(entity, entity.getLevel(), ray, false, false, filter));
    }

    public static Optional<HitResult> getTargetEntityOrBlock(Entity entity, double distance) {
        return getTargetEntityOrBlock(entity, distance, Entity.class);
    }

    @SuppressWarnings("Unchecked")
    public static Optional<HitResult> getTargetEntityOrBlock(Entity entity, double distance, Class<? extends Entity> entityClass) {
        return getTargetEntityOrBlock(entity, distance, new PredicateInstanceOf(entityClass));
    }

    public static Optional<HitResult> getTargetEntityOrBlock(Entity entity, double distance, Predicate<? super Entity> filter) {
        return getRayFromEyesAndDistance(entity, distance).flatMap(ray ->
                rayTraceForEntity(entity, entity.getLevel(), ray, false, false, filter));
    }

    public static Optional<BlockHitResult> rayTraceBlockForEntity(
            Entity entity, Level world, Tuple<Vec3,Vec3> ray, boolean stopOnLiquid, boolean ignoreUncollidable) {
        RayTraceOptions.Builder optionBuilder = getOptionBuilder().forEntity(entity);
        if (stopOnLiquid) {
            optionBuilder.stopOnAnyLiquid();
        } else {
            optionBuilder.ignoreLiquid();
        }
        if (ignoreUncollidable) {
            optionBuilder.ignoreUnCollidableBlocks();
        } else {
            optionBuilder.useUnCollidableBlocks();
        }
        return Optional.ofNullable(doRayTrace(world, ray, optionBuilder.build(), BLOCK_RAY_TRACER));
    }

    public static Optional<EntityHitResult> rayTraceEntityForEntity(
            Entity entity, Level world, Tuple<Vec3,Vec3> ray, boolean stopOnLiquid, boolean ignoreUncollidable) {
        return rayTraceEntityForEntity(entity, world, ray, stopOnLiquid, ignoreUncollidable, Entity.class);
    }

    @SuppressWarnings("Unchecked")
    public static Optional<EntityHitResult> rayTraceEntityForEntity(
            Entity entity, Level world, Tuple<Vec3,Vec3> ray, boolean stopOnLiquid, boolean ignoreUncollidable, Class<? extends Entity> entityClass) {
        return rayTraceEntityForEntity(entity, world, ray, stopOnLiquid, ignoreUncollidable, new PredicateInstanceOf(entityClass));
    }

    public static Optional<EntityHitResult> rayTraceEntityForEntity(
            Entity entity, Level world, Tuple<Vec3,Vec3> ray, boolean stopOnLiquid, boolean ignoreUncollidable, Predicate<? super Entity> filter) {

        RayTraceOptions.Builder optionBuilder = getOptionBuilder().forEntity(entity);
        optionBuilder.setEntityFilter(filter);
        if (stopOnLiquid) {
            optionBuilder.stopOnAnyLiquid();
        } else {
            optionBuilder.ignoreLiquid();
        }
        if (ignoreUncollidable) {
            optionBuilder.ignoreUnCollidableBlocks();
        } else {
            optionBuilder.useUnCollidableBlocks();
        }
        return Optional.ofNullable(doRayTrace(world, ray, optionBuilder.build(), ENTITY_RAY_TRACER));
    }

    public static Optional<HitResult> rayTraceForEntity(
            Entity entity, Level world, Tuple<Vec3,Vec3> ray, boolean stopOnLiquid, boolean ignoreUncollidable) {
        return rayTraceForEntity(entity, world, ray, stopOnLiquid, ignoreUncollidable, Entity.class);
    }

    @SuppressWarnings("Unchecked")
    public static Optional<HitResult> rayTraceForEntity(
            Entity entity, Level world, Tuple<Vec3,Vec3> ray, boolean stopOnLiquid, boolean ignoreUncollidable, Class<? extends Entity> entityClass) {
        return rayTraceForEntity(entity, world, ray, stopOnLiquid, ignoreUncollidable, new PredicateInstanceOf(entityClass));
    }

    public static Optional<HitResult> rayTraceForEntity(
            Entity entity, Level world, Tuple<Vec3,Vec3> ray, boolean stopOnLiquid, boolean ignoreUncollidable, Predicate<? super Entity> filter) {

        RayTraceOptions.Builder optionBuilder = getOptionBuilder().forEntity(entity);
        optionBuilder.setEntityFilter(filter);
        if (stopOnLiquid) {
            optionBuilder.stopOnAnyLiquid();
        } else {
            optionBuilder.ignoreLiquid();
        }
        if (ignoreUncollidable) {
            optionBuilder.ignoreUnCollidableBlocks();
        } else {
            optionBuilder.useUnCollidableBlocks();
        }
        return Optional.ofNullable(doRayTrace(world, ray, optionBuilder.build(), GENERAL_RAY_TRACER));
    }

    @Nullable
    private static <T extends HitResult> T doRayTrace(Level world, Tuple<Vec3, Vec3> ray, RayTraceOptions options, RayTracer<T> rayTracer) {
        Vec3 start = ray.getA();
        Vec3 stop = ray.getB();
        if (start.equals(stop)) {
            return rayTracer.createMiss(ray);
        } else {
            // Define start coordinates
            double x_1 = Mth.lerp(-1.0E-7D, start.x, stop.x);
            double y_1 = Mth.lerp(-1.0E-7D, start.y, stop.y);
            double z_1 = Mth.lerp(-1.0E-7D, start.z, stop.z);
            // Check if a result is found at the initial position
            int x_0 = Mth.floor(x_1);
            int y_0 = Mth.floor(y_1);
            int z_0 = Mth.floor(z_1);
            BlockPos.MutableBlockPos mutable_pos = new BlockPos.MutableBlockPos(x_0, y_0, z_0);
            T result = rayTracer.trace(world, x_1, y_1, z_1, mutable_pos, ray, options);
            if (result != null) {
                return result;
            }
            // Define end coordinates
            double x_2 = Mth.lerp(-1.0E-7D, stop.x, start.x);
            double y_2 = Mth.lerp(-1.0E-7D, stop.y, start.y);
            double z_2 = Mth.lerp(-1.0E-7D, stop.z, start.z);
            // Define iteration parameters
            double dx = x_2 - x_1;
            double dy = y_2 - y_1;
            double dz = z_2 - z_1;
            int xSign = Mth.sign(dx);
            int ySign = Mth.sign(dy);
            int zSign = Mth.sign(dz);
            double dxInv = xSign == 0 ? Double.MAX_VALUE : (double) xSign / dx;
            double dyInv = ySign == 0 ? Double.MAX_VALUE : (double) ySign / dy;
            double dzInv = zSign == 0 ? Double.MAX_VALUE : (double) zSign / dz;
            double progress_x = dxInv * (xSign > 0 ? 1.0D - Mth.frac(x_1) : Mth.frac(x_1));
            double progress_y = dyInv * (ySign > 0 ? 1.0D - Mth.frac(y_1) : Mth.frac(y_1));
            double progress_z = dzInv * (zSign > 0 ? 1.0D - Mth.frac(z_1) : Mth.frac(z_1));
            // Iterate along the ray
            while (progress_x <= 1.0D || progress_y <= 1.0D || progress_z <= 1.0D) {
                // Calculate next ray trace position
                if (progress_x < progress_y) {
                    if (progress_x < progress_z) {
                        x_0 += xSign;
                        progress_x += dxInv;
                    } else {
                        z_0 += zSign;
                        progress_z += dzInv;
                    }
                } else if (progress_y < progress_z) {
                    y_0 += ySign;
                    progress_y += dyInv;
                } else {
                    z_0 += zSign;
                    progress_z += dzInv;
                }
                // Check if a result has been found
                result = rayTracer.trace(world, x_1 + progress_x*dx, y_1+ progress_y*dy, z_1 + progress_z*dz,
                        mutable_pos.set(x_0, y_0, z_0), ray, options);
                if (result != null) {
                    return result;
                }
            }
            // No result has been found, return a miss
            return rayTracer.createMiss(ray);
        }
    }

    public static class PredicateInstanceOf<E extends Entity> implements Predicate<E> {
        private final Class<E> entityClass;

        public PredicateInstanceOf(Class<E> entityClass) {
            this.entityClass = entityClass;
        }

        @Override
        public boolean apply(@Nullable Entity e) {
            return e != null && entityClass.isAssignableFrom(e.getClass());
        }
    }

    private static Optional<Tuple<Vec3, Vec3>> getRayFromEyesAndDistance(Entity entity, double distance) {
        if(entity == null && !entity.isAlive()) {
            return Optional.empty();
        }
        Vec3 eyes = new Vec3(entity.getX(), entity.getY() + (double)entity.getEyeHeight(), entity.getZ());
        Vec3 look = entity.getLookAngle();
        if(look == null) {
            return Optional.empty();
        }
        Vec3 trace = eyes.add(look.x * distance, look.y * distance, look.z * distance);
        return Optional.of(new Tuple<>(eyes, trace));
    }

    public static RayTraceOptions.Builder getOptionBuilder() {
        return new RayTraceOptions.Builder();
    }

    public static class RayTraceOptions {
        private final ClipContext.Block blockMode;
        private final ClipContext.Fluid fluidMode;
        private final CollisionContext context;
        private final Predicate<? super Entity> entityFilter;

        private RayTraceOptions(ClipContext.Block blockMode, ClipContext.Fluid fluidMode, CollisionContext context, Predicate<? super Entity> entityFilter) {
            this.blockMode = blockMode;
            this.fluidMode = fluidMode;
            this.context = context;
            this.entityFilter = entityFilter;
        }

        public VoxelShape getBlockShape(LevelAccessor world, BlockPos pos, BlockState state) {
            return this.blockMode.get(state, world, pos, this.context);
        }

        public VoxelShape getFluidShape(LevelAccessor world, BlockPos pos, FluidState state) {
            return this.fluidMode.canPick(state) ? state.getShape(world, pos) : Shapes.empty();
        }

        protected Entity getSourceEntity() {
            if(this.context instanceof EntityCollisionContext) {
                return ((EntityCollisionContext) this.context).getEntity();
            }
            return null;
        }

        @Nullable
        public Entity getHitEntity(Level world, double x, double y, double z, Tuple<Vec3, Vec3> ray) {
            double d = 0.25D;
            AABB area = new AABB(x - d, y - d, z - d, x + d, y + d, z + d);
            List<Entity> entities = world.getEntities(this.getSourceEntity(), area, this.entityFilter);
            Entity closest = null;
            double dist = 999.0D * 999.0D;
            for(Entity collided : entities) {
                double distTo = (collided.getX() - ray.getA().x)*(collided.getX() -  ray.getA().x)
                        + (collided.getY() -  ray.getA().y)*(collided.getY() -  ray.getA().y)
                        + (collided.getZ()- ray.getA().z)*(collided.getZ() -  ray.getA().z);
                if(distTo < dist) {
                    dist = distTo;
                    closest = collided;
                }
            }
            return closest;
        }

        public static class Builder {
            ClipContext.Block blockMode;
            ClipContext.Fluid fluidMode;
            CollisionContext context;
            Predicate<? super Entity> entityFilter;

            private Builder() {
                this.blockMode = ClipContext.Block.OUTLINE;
                this.fluidMode = ClipContext.Fluid.NONE;
                this.context = CollisionContext.empty();
                this.entityFilter = Predicates.alwaysTrue();
            }

            public Builder forEntity(Entity entity) {
                this.context = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
                return this;
            }

            public Builder setEntityFilter(Predicate<? super Entity> filter) {
                this.entityFilter = filter;
                return this;
            }

            public Builder ignoreUnCollidableBlocks() {
                this.blockMode = ClipContext.Block.COLLIDER;
                return this;
            }

            public Builder useUnCollidableBlocks() {
                this.blockMode = ClipContext.Block.OUTLINE;
                return this;
            }

            public Builder useBlockVisual() {
                this.blockMode = ClipContext.Block.VISUAL;
                return this;
            }

            public Builder ignoreLiquid() {
                this.fluidMode = ClipContext.Fluid.NONE;
                return this;
            }

            public Builder stopOnLiquidSource() {
                this.fluidMode = ClipContext.Fluid.SOURCE_ONLY;
                return this;
            }

            public Builder stopOnAnyLiquid() {
                this.fluidMode = ClipContext.Fluid.ANY;
                return this;
            }

            public RayTraceOptions build() {
                return new RayTraceOptions(blockMode, fluidMode, context, entityFilter);
            }

        }
    }

    public static abstract class RayTracer<T extends HitResult> {
        @Nullable
        public abstract T trace(Level world, double x, double y, double z, BlockPos pos, Tuple<Vec3,Vec3> ray, RayTraceOptions options);

        public abstract T createMiss(Tuple<Vec3,Vec3> ray);
    }

    public static final RayTracer<BlockHitResult> BLOCK_RAY_TRACER = new RayTracer<BlockHitResult>() {
        @Nullable
        @Override
        public BlockHitResult trace(Level world, double x, double y, double z, BlockPos pos, Tuple<Vec3, Vec3> ray, RayTraceOptions options) {
            BlockState blockstate = world.getBlockState(pos);
            FluidState fluidstate = world.getFluidState(pos);
            VoxelShape blockShape = options.getBlockShape(world, pos, blockstate);
            BlockHitResult blockResult = world.clipWithInteractionOverride(ray.getA(), ray.getB(), pos, blockShape, blockstate);
            VoxelShape fluidShape = options.getFluidShape(world, pos, fluidstate);
            BlockHitResult fluidResult = fluidShape.clip(ray.getA(), ray.getB(), pos);
            double d0 = blockResult == null ? Double.MAX_VALUE : ray.getA().distanceToSqr(blockResult.getLocation());
            double d1 = fluidResult == null ? Double.MAX_VALUE : ray.getB().distanceToSqr(fluidResult.getLocation());
            return d0 <= d1 ? blockResult : fluidResult;
        }

        @Override
        public BlockHitResult createMiss(Tuple<Vec3, Vec3> ray) {
            Vec3 vector3d = ray.getA().subtract(ray.getB());
            return BlockHitResult.miss(ray.getB(), Direction.getNearest(vector3d.x, vector3d.y, vector3d.z), new BlockPos(ray.getB()));
        }
    };

    public static final RayTracer<EntityHitResult> ENTITY_RAY_TRACER = new RayTracer<>() {
        @Nullable
        @Override
        public EntityHitResult trace(Level world, double x, double y, double z, BlockPos pos, Tuple<Vec3, Vec3> ray, RayTraceOptions options) {
            Entity hit = options.getHitEntity(world, x, y, z, ray);
            return hit == null ? null : new EntityHitResult(hit, new Vec3(x, y, z));
        }

        @Override
        public EntityHitResult createMiss(Tuple<Vec3, Vec3> ray) {
            return new EntityRayTraceResultMiss(ray.getB());
        }
    };

    public static final RayTracer<HitResult> GENERAL_RAY_TRACER = new RayTracer<>() {
        @Nullable
        @Override
        public HitResult trace(Level world, double x, double y, double z, BlockPos pos, Tuple<Vec3, Vec3> ray, RayTraceOptions options) {
            EntityHitResult entityResult = ENTITY_RAY_TRACER.trace(world, x, y, z, pos, ray, options);
            if (entityResult != null) {
                return entityResult;
            }
            return BLOCK_RAY_TRACER.trace(world, x, y, z, pos, ray, options);
        }

        @Override
        public HitResult createMiss(Tuple<Vec3, Vec3> ray) {
            return BLOCK_RAY_TRACER.createMiss(ray);
        }
    };

    private static final class EntityRayTraceResultMiss extends EntityHitResult {
        public EntityRayTraceResultMiss(Vec3 hitVec) {
            super(null, hitVec);
        }

        @Nonnull
        @Override
        public Type getType() {
            return Type.MISS;
        }
    }
}
