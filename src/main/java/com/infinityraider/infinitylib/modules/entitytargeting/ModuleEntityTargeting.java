package com.infinityraider.infinitylib.modules.entitytargeting;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.modules.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class ModuleEntityTargeting extends Module {
    private static final ModuleEntityTargeting INSTANCE = new ModuleEntityTargeting();

    public static ModuleEntityTargeting getInstance() {
        return INSTANCE;
    }

    private final EntityTargetingHandler handler;

    private ModuleEntityTargeting() {
        this.handler = EntityTargetingHandler.getInstance();
    }

    public List<Object> getCommonEventHandlers() {
        return ImmutableList.of(this.handler);
    }

    public ModuleEntityTargeting registerEntityTargeting(Class<? extends Entity> target, Class<? extends Mob> aggressor) {
        this.handler.registerEntityTargetingRule(target, aggressor);
        return this;
    }
}
