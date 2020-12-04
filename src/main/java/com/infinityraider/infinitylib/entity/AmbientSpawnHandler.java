package com.infinityraider.infinitylib.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public final class AmbientSpawnHandler {
    private static final AmbientSpawnHandler INSTANCE = new AmbientSpawnHandler();

    public static AmbientSpawnHandler getInstance() {
        return INSTANCE;
    }

    private final Map<EntityClassification, Set<SpawnData>> spawnMap;

    private AmbientSpawnHandler() {
        this.spawnMap = Maps.newIdentityHashMap();
    }

    public AmbientSpawnHandler registerSpawnRule(EntityType<?> type, IInfinityLivingEntityType.SpawnRule rule) {
        Set<SpawnData> set = this.spawnMap.get(rule.classification());
        if(set == null) {
            set = Sets.newIdentityHashSet();
            this.spawnMap.put(rule.classification(), set);
        }
        set.add(new SpawnData(type, rule));
        return this;
    }

    @SubscribeEvent
    public void onPotentialSpawnEvent(WorldEvent.PotentialSpawns event) {
        if(!this.spawnMap.containsKey(event.getType())) {
            return;
        }
        final Biome biome = event.getWorld().getBiome(event.getPos());
        final BlockState state = event.getWorld().getBlockState(event.getPos().down());
        final IInfinityLivingEntityType.SpawnRule.Context context = new IInfinityLivingEntityType.SpawnRule.Context() {
            @Override
            public IWorld world() {
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

    private static final class SpawnData {
        private final MobSpawnInfo.Spawners spawnInfo;
        private final Predicate<IInfinityLivingEntityType.SpawnRule.Context> rule;

        private SpawnData(EntityType<?> type, IInfinityLivingEntityType.SpawnRule rule) {
            this.spawnInfo = new MobSpawnInfo.Spawners(type, rule.weight(), rule.min(), rule.max());
            this.rule = rule.spawnRule();
        }

        private boolean canSpawnAt(IInfinityLivingEntityType.SpawnRule.Context context) {
            return this.rule.test(context);
        }

        private MobSpawnInfo.Spawners getSpawnInfo() {
            return this.spawnInfo;
        }
    }
}
