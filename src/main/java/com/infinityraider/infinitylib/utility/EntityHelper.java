package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
                try {
                    priorityField.set(goal, ((int) priorityField.get(goal)) + 1);
                    return true;
                } catch(Exception e) {
                    InfinityLib.instance.getLogger().error("Failed to increment priority");
                    InfinityLib.instance.getLogger().printStackTrace(e);
                    return false;
                }
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
        try {
            return ObfuscationReflectionHelper.findField(GoalSelector.class, "field_220892_d");
        } catch (Exception e) {
            InfinityLib.instance.getLogger().error("Could not retrieve Goals field from GoalSelector.class");
            InfinityLib.instance.getLogger().printStackTrace(e);
            return null;
        }
    }

    //TODO: fix this if reflection does not work
    private static Field initPriorityField() {
        try {
            // Retrieve field
            Field field = ObfuscationReflectionHelper.findField(WrappedGoal.class, "field_220775_b");
            // Remove final modifier
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            // Return the field
            return field;
        } catch (Exception e) {
            InfinityLib.instance.getLogger().error("Could not retrieve Priority field from PrioritizedGoal.class");
            InfinityLib.instance.getLogger().printStackTrace(e);
            return null;
        }
    }
}
