package com.infinityraider.infinitylib.render.item;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        this.defaultRenderer = ItemStackTileEntityRenderer.instance::func_239207_a_;
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

    public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
        return () -> () -> new Dispatcher(this);
    }

    private static class Dispatcher extends ItemStackTileEntityRenderer {
        private final InfItemRendererRegistry registry;
        private InfItemRenderer renderer;

        private Dispatcher(InfItemRendererRegistry registry) {
            this.registry = registry;
        }

        private InfItemRenderer getRenderer(ItemStack stack) {
            if(this.renderer == null) {
                this.renderer = this.registry.getRenderer(stack);
            }
            return this.renderer;
        }

        @Override
        public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType perspective, MatrixStack transforms,
                                   IRenderTypeBuffer buffer, int light, int overlay) {
            this.getRenderer(stack).render(stack, perspective, transforms, buffer, light, overlay);
        }
    }
}
