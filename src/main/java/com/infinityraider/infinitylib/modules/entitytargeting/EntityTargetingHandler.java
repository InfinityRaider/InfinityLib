package com.infinityraider.infinitylib.modules.entitytargeting;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTargetingHandler {
    private static final EntityTargetingHandler INSTANCE = new EntityTargetingHandler();

    static EntityTargetingHandler getInstance() {
        return INSTANCE;
    }

    private Map<Class<? extends Mob>, List<Class<? extends Entity>>> targetMap;

    private EntityTargetingHandler() {
        this.targetMap = new HashMap<>();
    }

    void registerEntityTargetingRule(Class<? extends Entity> target, Class<? extends Mob> aggressor) {
        if(!this.targetMap.containsKey(aggressor)) {
            this.targetMap.put(aggressor, new ArrayList<>());
        }
        this.targetMap.get(aggressor).add(target);
    }

    @SubscribeEvent
    @SuppressWarnings({"unused", "unchecked"})
    public void onZombieSpawn(LivingSpawnEvent event) {
        if(!(event.getEntity() instanceof Mob)) {
            return;
        }
        Mob mob = (Mob) event.getEntity();
        //if i replace this with a stream it stops working for some reason >>
        for(Map.Entry<Class<? extends Mob>, List<Class<? extends Entity>>> entry : this.targetMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(mob.getClass())) {
                for (Class<? extends Entity> entityClass : entry.getValue()) {
                    mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal(mob, entityClass, true));
                }
            }
        }
    }
}
