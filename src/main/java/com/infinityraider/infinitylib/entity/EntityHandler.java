package com.infinityraider.infinitylib.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class EntityHandler {
    private static final EntityHandler INSTANCE = new EntityHandler();

    public static EntityHandler getInstance() {
        return INSTANCE;
    }

    private final Map<MobCategory, Set<SpawnData>> spawnMap;
    private final Map<EntityType<? extends LivingEntity>, AttributeSupplier> attributeMap;

    private EntityHandler() {
        this.spawnMap = Maps.newIdentityHashMap();
        this.attributeMap = Maps.newIdentityHashMap();
    }

    public <E extends Mob> EntityHandler registerSpawnRule(EntityType<E> type, IMobEntityType.SpawnRule rule) {
        // Register spawn placement rule
        SpawnPlacements.register(type, rule.spawnType(), rule.heightType(), new SpawnPlacements.SpawnPredicate<E>() {
            @Override
            public boolean test(EntityType<E> entityType, ServerLevelAccessor world, MobSpawnType spawnType, BlockPos pos, Random random) {
                return rule.canSpawn(world, spawnType, pos, random);
            }
        });
        // Add to set of spawn rules to add to biome spawns
        Set<SpawnData> set = this.spawnMap.get(rule.classification());
        if(set == null) {
            set = Sets.newIdentityHashSet();
            this.spawnMap.put(rule.classification(), set);
        }
        set.add(new SpawnData(type, rule));
        return this;
    }

    public EntityHandler registerAttribute(EntityType<? extends LivingEntity> type, AttributeSupplier attribute) {
        this.attributeMap.put(type, attribute);
        return this;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBiomeLoad(BiomeLoadingEvent event) {
        this.spawnMap.forEach((type, value) -> value.stream()
                .filter(data -> data.checkBiome(event.getName(), event.getClimate(), event.getCategory(), event.getEffects()))
                .forEach(data -> event.getSpawns().addSpawn(type, data.getSpawnInfo())));
    }

    private static final class SpawnData {
        private final MobSpawnSettings.SpawnerData spawnInfo;
        private final IMobEntityType.SpawnRule rule;

        private SpawnData(EntityType<?> type, IMobEntityType.SpawnRule rule) {
            this.spawnInfo = new MobSpawnSettings.SpawnerData(type, rule.weight(), rule.min(), rule.max());
            this.rule = rule;
        }

        private boolean checkBiome(@Nullable ResourceLocation biomeId, Biome.ClimateSettings climate, Biome.BiomeCategory category, BiomeSpecialEffects effects) {
            return this.rule.biomeCheck(biomeId, climate, category, effects);
        }

        private MobSpawnSettings.SpawnerData getSpawnInfo() {
            return this.spawnInfo;
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onRegisterEntityAttributes(EntityAttributeCreationEvent event) {
        this.attributeMap.forEach(event::put);
    }
}
