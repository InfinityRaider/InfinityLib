package com.infinityraider.infinitylib.entity;

import com.infinityraider.infinitylib.modules.entitytargeting.ModuleEntityTargeting;
import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public interface IInfinityEntityType extends IInfinityRegistrable<EntityType<?>> {
    /**
     * @return the class for the entity of this type
     */
    Class<? extends Entity> getEntityClass();


    /**
     * Defines which entities should target the entities of this type
     * @param aggressors list containing classes to target this entity type
     * @return this
     */
    default IInfinityEntityType setEntityTargetedBy(Class<? extends MobEntity>... aggressors) {
        ModuleEntityTargeting module = ModuleEntityTargeting.getInstance();
        module.activate();
        for(Class<? extends MobEntity> aggressor : aggressors) {
            module.registerEntityTargeting(this.getEntityClass(), aggressor);
        }
        return this;
    }

    <T extends Entity> IRenderFactory<T> getRenderFactory();


}
