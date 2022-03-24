package com.infinityraider.infinitylib.modules.synchronizedeffects;

import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.reference.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import javax.annotation.Nullable;

public class CapabilityEffectTracker implements IInfSerializableCapabilityImplementation<LivingEntity, EffectTracker> {
    private static final CapabilityEffectTracker INSTANCE = new CapabilityEffectTracker();

    public static CapabilityEffectTracker getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "potion_tracker");

    public static Capability<EffectTracker> CAPABILITY_POTION_TRACKER = CapabilityManager.get(new CapabilityToken<>(){});

    private CapabilityEffectTracker() {}

    @Override
    public Capability<EffectTracker> getCapability() {
        return CAPABILITY_POTION_TRACKER;
    }

    @Override
    public boolean shouldApplyCapability(LivingEntity carrier) {
        return true;
    }

    @Override
    public EffectTracker createNewValue(LivingEntity carrier) {
        return new EffectTracker(carrier);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<LivingEntity> getCarrierClass() {
        return LivingEntity.class;
    }

    @Override
    public Class<EffectTracker> getCapabilityClass() {
        return EffectTracker.class;
    }

    @Nullable
    public static EffectTracker getEffectTracker(LivingEntity entity) {
        return entity.getCapability(CAPABILITY_POTION_TRACKER, null).orElse(null);
    }
}
