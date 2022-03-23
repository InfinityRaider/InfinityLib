package com.infinityraider.infinitylib.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

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

    public EntityHandler registerSpawnRule(EntityType<?> type, IMobEntityType.SpawnRule rule) {
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

    // TODO: bring this back once Forge reimplements it
    /*
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPotentialSpawnEvent(PotentialSpawns event) {
        if(!this.spawnMap.containsKey(event.getType())) {
            return;
        }
        final Biome biome = event.getWorld().getBiome(event.getPos());
        final BlockState state = event.getWorld().getBlockState(event.getPos().down());
        final IMobEntityType.SpawnRule.Context context = new IMobEntityType.SpawnRule.Context() {
            @Override
            public Level world() {
                return event.getWorld();
            }

            @Override
            public BlockPos pos() {
                return event.getPos();
            }

            @Override
            public BlockState stateBelow() {
                return state;
            }

            @Override
            public Biome biome() {
                return biome;
            }
        };
        this.spawnMap.get(event.getType()).stream()
                .filter(data -> data.canSpawnAt(context))
                .map(SpawnData::getSpawnInfo)
                .forEach(info -> event.getList().add(info));
    }
     */

    private static final class SpawnData {
        private final MobSpawnSettings.SpawnerData spawnInfo;
        private final Predicate<IMobEntityType.SpawnRule.Context> rule;

        private SpawnData(EntityType<?> type, IMobEntityType.SpawnRule rule) {
            this.spawnInfo = new MobSpawnSettings.SpawnerData(type, rule.weight(), rule.min(), rule.max());
            this.rule = rule.spawnRule();
        }

        private boolean canSpawnAt(IMobEntityType.SpawnRule.Context context) {
            return this.rule.test(context);
        }

        private MobSpawnSettings.SpawnerData getSpawnInfo() {
            return this.spawnInfo;
        }
    }

    @SubscribeEvent
    public void onRegisterEntityAttributes(EntityAttributeCreationEvent event) {
        this.attributeMap.forEach(event::put);
    }
}
