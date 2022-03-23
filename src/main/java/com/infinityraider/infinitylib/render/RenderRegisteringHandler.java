package com.infinityraider.infinitylib.render;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.entity.IEntityRenderSupplier;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RenderRegisteringHandler {
    private static final RenderRegisteringHandler INSTANCE = new RenderRegisteringHandler();

    public static RenderRegisteringHandler getInstance() {
        return INSTANCE;
    }

    private final Map<BlockEntityType<? extends BlockEntity>, BlockEntityRendererProvider<? extends BlockEntity>> tileRenderers;
    private final Map<EntityType<? extends Entity>, EntityRendererProvider<? extends Entity>> entityRenderers;

    private RenderRegisteringHandler() {
        // Concurrent due to parallel mod loading
        this.tileRenderers = Maps.newConcurrentMap();
        this.entityRenderers = Maps.newConcurrentMap();
    }

    public <T extends BlockEntity> void register(BlockEntityType<T> type, BlockEntityRendererProvider<T> renderer) {
        tileRenderers.put(type, renderer);
    }

    public <T extends Entity> void register(EntityType<T> type, IEntityRenderSupplier<T> renderer) {
        entityRenderers.put(type, renderer.supplyRenderer().get());
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onRendererRegistration(EntityRenderersEvent.RegisterRenderers event) {
        this.tileRenderers.entrySet().forEach(entry -> registerTile(event, entry));
        this.entityRenderers.entrySet().forEach(entry -> registerEntity(event, entry));
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> void registerTile(EntityRenderersEvent.RegisterRenderers event, Map.Entry<BlockEntityType<?>, BlockEntityRendererProvider<?>> entry) {
        event.registerBlockEntityRenderer((BlockEntityType<T>) entry.getKey(), (BlockEntityRendererProvider<T>) entry.getValue());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> void registerEntity(EntityRenderersEvent.RegisterRenderers event, Map.Entry<EntityType<?>, EntityRendererProvider<?>> entry) {
        event.registerEntityRenderer((EntityType<T>) entry.getKey(), (EntityRendererProvider<T>) entry.getValue());
    }


}
