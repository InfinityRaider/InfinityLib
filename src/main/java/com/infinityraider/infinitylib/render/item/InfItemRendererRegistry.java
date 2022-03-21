package com.infinityraider.infinitylib.render.item;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class InfItemRendererRegistry {
    private static final InfItemRendererRegistry INSTANCE = new InfItemRendererRegistry();

    public static InfItemRendererRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<Item, InfItemRenderer> registry;
    private final InfItemRenderer defaultRenderer;

    private InfItemRendererRegistry() {
        this.registry = Maps.newIdentityHashMap();
        this.defaultRenderer = Minecraft.getInstance().getItemRenderer().getBlockEntityRenderer()::renderByItem;
    }

    public InfItemRendererRegistry register(IInfinityItem item) {
        InfItemRenderer renderer = item.getItemRenderer();
        if(renderer != null) {
            this.registry.put(item.cast(), renderer);
        }
        return this;
    }

    private InfItemRenderer getRenderer(ItemStack stack) {
        return this.registry.getOrDefault(stack.getItem(), this.defaultRenderer);
    }

    public Supplier<Callable<BlockEntityWithoutLevelRenderer>> getISTER() {
        return () -> () -> new Dispatcher(this);
    }

    private static class Dispatcher extends BlockEntityWithoutLevelRenderer {
        private final InfItemRendererRegistry registry;
        private InfItemRenderer renderer;

        private Dispatcher(InfItemRendererRegistry registry) {
            super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
            this.registry = registry;
        }

        private InfItemRenderer getRenderer(ItemStack stack) {
            if(this.renderer == null) {
                this.renderer = this.registry.getRenderer(stack);
            }
            return this.renderer;
        }

        @Override
        public void renderByItem(ItemStack stack, ItemTransforms.TransformType perspective, PoseStack transforms,
                                 MultiBufferSource buffer, int light, int overlay) {
            this.getRenderer(stack).render(stack, perspective, transforms, buffer, light, overlay);
        }
    }
}
