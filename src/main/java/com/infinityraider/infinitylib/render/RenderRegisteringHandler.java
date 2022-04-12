package com.infinityraider.infinitylib.render;

import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.block.tile.IInfinityTileEntityType;
import com.infinityraider.infinitylib.entity.EmptyEntityRenderSupplier;
import com.infinityraider.infinitylib.entity.IInfinityEntityType;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.infinityraider.infinitylib.render.tile.TileEntityRendererWrapper;
import com.infinityraider.infinitylib.utility.ReflectionHelper;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class RenderRegisteringHandler {
    private static final RenderRegisteringHandler INSTANCE = new RenderRegisteringHandler();

    public static RenderRegisteringHandler getInstance() {
        return INSTANCE;
    }

    private final Set<ModContentRegistry> tileRegistries;
    private final Set<ModContentRegistry> entityRegistries;

    private RenderRegisteringHandler() {
        // Concurrent due to parallel mod loading
        this.tileRegistries = Sets.newConcurrentHashSet();
        this.entityRegistries = Sets.newConcurrentHashSet();
    }

    public void registerTileRegistry(@Nullable ModContentRegistry tileRegistry) {
        if(tileRegistry != null) {
            this.tileRegistries.add(tileRegistry);
        }
    }

    public void registerEntityRegistry(@Nullable ModContentRegistry entityRegistry) {
        if(entityRegistry != null) {
            this.entityRegistries.add(entityRegistry);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // TODO: fix reflection for registration
        this.tileRegistries.forEach(registry -> ReflectionHelper.forEachValueIn(registry, IInfinityTileEntityType.class, type -> registerTileRenderer(event, type)));
        this.entityRegistries.forEach(registry -> ReflectionHelper.forEachValueIn(registry, IInfinityEntityType.class, type -> registerEntityRenderer(event, type)));
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> void registerTileRenderer(EntityRenderersEvent.RegisterRenderers event, IInfinityTileEntityType type) {
        // Create Renderer
        ITileRenderer<? extends BlockEntity> renderer = type.getRenderer();
        if (renderer != null) {
            // Register TileEntityRendererWrapper
            event.registerBlockEntityRenderer(type.cast(), ((dispatcher) -> TileEntityRendererWrapper.createWrapper(renderer)));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> void registerEntityRenderer(EntityRenderersEvent.RegisterRenderers event, IInfinityEntityType type) {
        if (type.getRenderSupplier() == null) {
            InfinityLib.instance.getLogger().info("", "No entity rendering factory was found for entity " + type.getInternalName());
            event.registerEntityRenderer(type.cast(), EmptyEntityRenderSupplier.getInstance().supplyRenderer().get());
        } else {
            event.registerEntityRenderer(type.cast(), type.getRenderSupplier().supplyRenderer().get());
        }
    }
}
