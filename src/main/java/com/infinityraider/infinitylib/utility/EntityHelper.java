package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class EntityHelper {
    private static final Field goalsField;
    private static final Field priorityField;

    public static boolean injectGoal(Mob entity, Goal goal, int priority) {
        if(incrementPriorities(entity.goalSelector, priority)) {
            entity.goalSelector.addGoal(priority, goal);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static boolean incrementPriorities(GoalSelector selector, int priority) {
        if(goalsField == null) {
            InfinityLib.instance.getLogger().error("Could not increment priorities, goals field not retrieved");
            return false;
        }
        if(priorityField == null) {
            InfinityLib.instance.getLogger().error("Could not increment priorities, priority field not retrieved");
            return false;
        }
        try {
            Set<WrappedGoal> goals = (Set<WrappedGoal>) goalsField.get(selector);
            return goals.stream().filter(goal -> goal.getPriority() >= priority).allMatch(goal -> {
                boolean success = false;
                try {
                    success = UnsafeUtil.getInstance().replaceField(priorityField, goal, ((int) priorityField.get(goal)) + 1);
                } catch(Exception e) {
                    InfinityLib.instance.getLogger().error("Failed to increment priority");
                    InfinityLib.instance.getLogger().printStackTrace(e);
                }
                return success;
            });
        } catch (Exception e) {
            InfinityLib.instance.getLogger().error("Encountered error while getting the goals from the GoalSelector");
            InfinityLib.instance.getLogger().printStackTrace(e);
            return false;
        }
    }

    static {
        goalsField = initGoalsField();
        priorityField = initPriorityField();
    }

    @Nullable
    private static Field initGoalsField() {
        return Arrays.stream(GoalSelector.class.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> field.getType() == Set.class)
                .findFirst()
                .orElseGet(() -> {
                    InfinityLib.instance.getLogger().error("Could not retrieve Goals field from GoalSelector.class");
                    return null;
                });
    }

    @Nullable
    private static Field initPriorityField() {
        return Arrays.stream(WrappedGoal.class.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> field.getType() == int.class)
                .findFirst()
                .orElseGet(() -> {
                    InfinityLib.instance.getLogger().error("Could not retrieve Priority field from PrioritizedGoal.class");
                    return null;
                });
    }

    @SuppressWarnings("unchecked")
    public static SynchedEntityData defineEntityData(Entity entity) {
        SynchedEntityData entityData = new SynchedEntityData(entity);
        Arrays.stream(Entity.class.getDeclaredFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.getType() == EntityDataAccessor.class)
                .peek(field ->field.setAccessible(true))
                .map(field -> {
                    try {
                        return (EntityDataAccessor<?>) field.get(null);
                    } catch (Exception e) {
                        InfinityLib.instance.getLogger().error("Failed to fetch entity data accessor: " + field.getName());
                        InfinityLib.instance.getLogger().printStackTrace(e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(accessor -> {
                    EntityDataSerializer<?> serializer = accessor.getSerializer();
                    if (serializer == EntityDataSerializers.BYTE) {
                        entityData.define((EntityDataAccessor<Byte>) accessor, (byte) 0);
                    } else if (serializer == EntityDataSerializers.INT) {
                        entityData.define((EntityDataAccessor<Integer>) accessor, 0);
                    } else if (serializer == EntityDataSerializers.OPTIONAL_COMPONENT) {
                        entityData.define((EntityDataAccessor<Optional<Component>>) accessor, Optional.empty());
                    } else if (serializer == EntityDataSerializers.BOOLEAN) {
                        entityData.define((EntityDataAccessor<Boolean>) accessor, false);
                    } else if (serializer == EntityDataSerializers.POSE) {
                        entityData.define((EntityDataAccessor<Pose>) accessor, Pose.STANDING);
                    }
                });
        return entityData;
    }
}
