package com.infinityraider.infinitylib.utility;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class RayTraceHelper {
    public static Optional<BlockRayTraceResult> getTargetBlock(Entity entity, double distance) {
        return getRayFromEyesAndDistance(entity, distance).flatMap(ray ->
                rayTraceBlockForEntity(entity, entity.getEntityWorld(), ray, false, false));
    }

    public static Optional<EntityRayTraceResult> getTargetEntity(Entity entity, double distance) {
        return getTargetEntity(entity, distance, Entity.class);
    }

    @SuppressWarnings("Unchecked")
    public static Optional<EntityRayTraceResult> getTargetEntity(Entity entity, double distance, Class<? extends Entity> entityClass) {
        return getTargetEntity(entity, distance, new PredicateInstanceOf(entityClass));
    }

    public static Optional<EntityRayTraceResult> getTargetEntity(Entity entity, double distance, Predicate<? super Entity> filter) {
        return getRayFromEyesAndDistance(entity, distance).flatMap(ray ->
                rayTraceEntityForEntity(entity, entity.getEntityWorld(), ray, false, false, filter));
    }

    public static Optional<RayTraceResult> getTargetEntityOrBlock(Entity entity, double distance) {
        return getTargetEntityOrBlock(entity, distance, Entity.class);
    }

    @SuppressWarnings("Unchecked")
    public static Optional<RayTraceResult> getTargetEntityOrBlock(Entity entity, double distance, Class<? extends Entity> entityClass) {
        return getTargetEntityOrBlock(entity, distance, new PredicateInstanceOf(entityClass));
    }

    public static Optional<RayTraceResult> getTargetEntityOrBlock(Entity entity, double distance, Predicate<? super Entity> filter) {
        return getRayFromEyesAndDistance(entity, distance).flatMap(ray ->
                rayTraceForEntity(entity, entity.getEntityWorld(), ray, false, false, filter));
    }

    public static Optional<BlockRayTraceResult> rayTraceBlockForEntity(
            Entity entity, World world, Tuple<Vector3d,Vector3d> ray, boolean stopOnLiquid, boolean ignoreUncollidable) {
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

    public static Optional<EntityRayTraceResult> rayTraceEntityForEntity(
            Entity entity, World world, Tuple<Vector3d,Vector3d> ray, boolean stopOnLiquid, boolean ignoreUncollidable) {
        return rayTraceEntityForEntity(entity, world, ray, stopOnLiquid, ignoreUncollidable, Entity.class);
    }

    @SuppressWarnings("Unchecked")
    public static Optional<EntityRayTraceResult> rayTraceEntityForEntity(
            Entity entity, World world, Tuple<Vector3d,Vector3d> ray, boolean stopOnLiquid, boolean ignoreUncollidable, Class<? extends Entity> entityClass) {
        return rayTraceEntityForEntity(entity, world, ray, stopOnLiquid, ignoreUncollidable, new PredicateInstanceOf(entityClass));
    }

    public static Optional<EntityRayTraceResult> rayTraceEntityForEntity(
            Entity entity, World world, Tuple<Vector3d,Vector3d> ray, boolean stopOnLiquid, boolean ignoreUncollidable, Predicate<? super Entity> filter) {

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

    public static Optional<RayTraceResult> rayTraceForEntity(
            Entity entity, World world, Tuple<Vector3d,Vector3d> ray, boolean stopOnLiquid, boolean ignoreUncollidable) {
        return rayTraceForEntity(entity, world, ray, stopOnLiquid, ignoreUncollidable, Entity.class);
    }

    @SuppressWarnings("Unchecked")
    public static Optional<RayTraceResult> rayTraceForEntity(
            Entity entity, World world, Tuple<Vector3d,Vector3d> ray, boolean stopOnLiquid, boolean ignoreUncollidable, Class<? extends Entity> entityClass) {
        return rayTraceForEntity(entity, world, ray, stopOnLiquid, ignoreUncollidable, new PredicateInstanceOf(entityClass));
    }

    public static Optional<RayTraceResult> rayTraceForEntity(
            Entity entity, World world, Tuple<Vector3d,Vector3d> ray, boolean stopOnLiquid, boolean ignoreUncollidable, Predicate<? super Entity> filter) {

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
    private static <T extends RayTraceResult> T doRayTrace(World world, Tuple<Vector3d, Vector3d> ray, RayTraceOptions options, RayTracer<T> rayTracer) {
        Vector3d start = ray.getA();
        Vector3d stop = ray.getB();
        if (start.equals(stop)) {
            return rayTracer.createMiss(ray);
        } else {
            // Define start coordinates
            double x_1 = MathHelper.lerp(-1.0E-7D, start.x, stop.x);
            double y_1 = MathHelper.lerp(-1.0E-7D, start.y, stop.y);
            double z_1 = MathHelper.lerp(-1.0E-7D, start.z, stop.z);
            // Check if a result is found at the initial position
            int x_0 = MathHelper.floor(x_1);
            int y_0 = MathHelper.floor(y_1);
            int z_0 = MathHelper.floor(z_1);
            BlockPos.Mutable mutable_pos = new BlockPos.Mutable(x_0, y_0, z_0);
            T result = rayTracer.trace(world, x_1, y_1, z_1, mutable_pos, ray, options);
            if (result != null) {
                return result;
            }
            // Define end coordinates
            double x_2 = MathHelper.lerp(-1.0E-7D, stop.x, start.x);
            double y_2 = MathHelper.lerp(-1.0E-7D, stop.y, start.y);
            double z_2 = MathHelper.lerp(-1.0E-7D, stop.z, start.z);
            // Define iteration parameters
            double dx = x_2 - x_1;
            double dy = y_2 - y_1;
            double dz = z_2 - z_1;
            int xSign = MathHelper.signum(dx);
            int ySign = MathHelper.signum(dy);
            int zSign = MathHelper.signum(dz);
            double dxInv = xSign == 0 ? Double.MAX_VALUE : (double) xSign / dx;
            double dyInv = ySign == 0 ? Double.MAX_VALUE : (double) ySign / dy;
            double dzInv = zSign == 0 ? Double.MAX_VALUE : (double) zSign / dz;
            double progress_x = dxInv * (xSign > 0 ? 1.0D - MathHelper.frac(x_1) : MathHelper.frac(x_1));
            double progress_y = dyInv * (ySign > 0 ? 1.0D - MathHelper.frac(y_1) : MathHelper.frac(y_1));
            double progress_z = dzInv * (zSign > 0 ? 1.0D - MathHelper.frac(z_1) : MathHelper.frac(z_1));
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
                        mutable_pos.setPos(x_0, y_0, z_0), ray, options);
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

    private static Optional<Tuple<Vector3d, Vector3d>> getRayFromEyesAndDistance(Entity entity, double distance) {
        if(entity == null && !entity.isAlive()) {
            return Optional.empty();
        }
        Vector3d eyes = new Vector3d(entity.getPosX(), entity.getPosY() + (double)entity.getEyeHeight(), entity.getPosZ());
        Vector3d look = entity.getLookVec();
        if(look == null) {
            return Optional.empty();
        }
        Vector3d trace = eyes.add(look.x * distance, look.y * distance, look.z * distance);
        return Optional.of(new Tuple<>(eyes, trace));
    }

    public static RayTraceOptions.Builder getOptionBuilder() {
        return new RayTraceOptions.Builder();
    }

    public static class RayTraceOptions {
        private final RayTraceContext.BlockMode blockMode;
        private final RayTraceContext.FluidMode fluidMode;
        private final ISelectionContext context;
        private final Predicate<? super Entity> entityFilter;

        private RayTraceOptions(RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode, ISelectionContext context, Predicate<? super Entity> entityFilter) {
            this.blockMode = blockMode;
            this.fluidMode = fluidMode;
            this.context = context;
            this.entityFilter = entityFilter;
        }

        public VoxelShape getBlockShape(IBlockReader world, BlockPos pos, BlockState state) {
            return this.blockMode.get(state, world, pos, this.context);
        }

        public VoxelShape getFluidShape(IBlockReader world, BlockPos pos, FluidState state) {
            return this.fluidMode.test(state) ? state.getShape(world, pos) : VoxelShapes.empty();
        }

        @Nullable
        public Entity getHitEntity(World world, double x, double y, double z, Tuple<Vector3d, Vector3d> ray) {
            double d = 0.25D;
            AxisAlignedBB area = new AxisAlignedBB(x - d, y - d, z - d, x + d, y + d, z + d);
            List<Entity> entities = world.getEntitiesInAABBexcluding(this.context.getEntity(), area, this.entityFilter);
            Entity closest = null;
            double dist = 999.0D * 999.0D;
            for(Entity collided : entities) {
                double distTo = (collided.getPosX() - ray.getA().x)*(collided.getPosX() -  ray.getA().x)
                        + (collided.getPosY() -  ray.getA().y)*(collided.getPosY() -  ray.getA().y)
                        + (collided.getPosZ()- ray.getA().z)*(collided.getPosZ() -  ray.getA().z);
                if(distTo < dist) {
                    dist = distTo;
                    closest = collided;
                }
            }
            return closest;
        }

        public static class Builder {
            RayTraceContext.BlockMode blockMode;
            RayTraceContext.FluidMode fluidMode;
            ISelectionContext context;
            Predicate<? super Entity> entityFilter;

            private Builder() {
                this.blockMode = RayTraceContext.BlockMode.OUTLINE;
                this.fluidMode = RayTraceContext.FluidMode.NONE;
                this.context = ISelectionContext.dummy();
                this.entityFilter = Predicates.alwaysTrue();
            }

            public Builder forEntity(Entity entity) {
                this.context = entity == null ? ISelectionContext.dummy() : ISelectionContext.forEntity(entity);
                return this;
            }

            public Builder setEntityFilter(Predicate<? super Entity> filter) {
                this.entityFilter = filter;
                return this;
            }

            public Builder ignoreUnCollidableBlocks() {
                this.blockMode = RayTraceContext.BlockMode.COLLIDER;
                return this;
            }

            public Builder useUnCollidableBlocks() {
                this.blockMode = RayTraceContext.BlockMode.OUTLINE;
                return this;
            }

            public Builder useBlockVisual() {
                this.blockMode = RayTraceContext.BlockMode.VISUAL;
                return this;
            }

            public Builder ignoreLiquid() {
                this.fluidMode = RayTraceContext.FluidMode.NONE;
                return this;
            }

            public Builder stopOnLiquidSource() {
                this.fluidMode = RayTraceContext.FluidMode.SOURCE_ONLY;
                return this;
            }

            public Builder stopOnAnyLiquid() {
                this.fluidMode = RayTraceContext.FluidMode.ANY;
                return this;
            }

            public RayTraceOptions build() {
                return new RayTraceOptions(blockMode, fluidMode, context, entityFilter);
            }

        }
    }

    public static abstract class RayTracer<T extends RayTraceResult> {
        @Nullable
        public abstract T trace(World world, double x, double y, double z, BlockPos pos, Tuple<Vector3d,Vector3d> ray, RayTraceOptions options);

        public abstract T createMiss(Tuple<Vector3d,Vector3d> ray);
    }

    public static final RayTracer<BlockRayTraceResult> BLOCK_RAY_TRACER = new RayTracer<BlockRayTraceResult>() {
        @Nullable
        @Override
        public BlockRayTraceResult trace(World world, double x, double y, double z, BlockPos pos, Tuple<Vector3d, Vector3d> ray, RayTraceOptions options) {
            BlockState blockstate = world.getBlockState(pos);
            FluidState fluidstate = world.getFluidState(pos);
            VoxelShape blockShape = options.getBlockShape(world, pos, blockstate);
            BlockRayTraceResult blockResult = world.rayTraceBlocks(ray.getA(), ray.getB(), pos, blockShape, blockstate);
            VoxelShape fluidShape = options.getFluidShape(world, pos, fluidstate);
            BlockRayTraceResult fluidResult = fluidShape.rayTrace(ray.getA(), ray.getB(), pos);
            double d0 = blockResult == null ? Double.MAX_VALUE : ray.getA().squareDistanceTo(blockResult.getHitVec());
            double d1 = fluidResult == null ? Double.MAX_VALUE : ray.getB().squareDistanceTo(fluidResult.getHitVec());
            return d0 <= d1 ? blockResult : fluidResult;
        }

        @Override
        public BlockRayTraceResult createMiss(Tuple<Vector3d, Vector3d> ray) {
            Vector3d vector3d = ray.getA().subtract(ray.getB());
            return BlockRayTraceResult.createMiss(ray.getB(), Direction.getFacingFromVector(vector3d.x, vector3d.y, vector3d.z), new BlockPos(ray.getB()));
        }
    };

    public static final RayTracer<EntityRayTraceResult> ENTITY_RAY_TRACER = new RayTracer<EntityRayTraceResult>() {
        @Nullable
        @Override
        public EntityRayTraceResult trace(World world, double x, double y, double z, BlockPos pos, Tuple<Vector3d, Vector3d> ray, RayTraceOptions options) {
            Entity hit = options.getHitEntity(world, x, y, z, ray);
            return hit == null ? null : new EntityRayTraceResult(hit, new Vector3d(x, y, z));
        }

        @Override
        public EntityRayTraceResult createMiss(Tuple<Vector3d, Vector3d> ray) {
            return new EntityRayTraceResult(null, ray.getB());
        }
    };

    public static final RayTracer<RayTraceResult> GENERAL_RAY_TRACER = new RayTracer<RayTraceResult>() {
        @Nullable
        @Override
        public RayTraceResult trace(World world, double x, double y, double z, BlockPos pos, Tuple<Vector3d, Vector3d> ray, RayTraceOptions options) {
            EntityRayTraceResult entityResult = ENTITY_RAY_TRACER.trace(world, x, y, z, pos, ray, options);
            if (entityResult != null) {
                return entityResult;
            }
            return BLOCK_RAY_TRACER.trace(world, x, y, z, pos, ray, options);
        }

        @Override
        public RayTraceResult createMiss(Tuple<Vector3d, Vector3d> ray) {
            return BLOCK_RAY_TRACER.createMiss(ray);
        }
    };
}
