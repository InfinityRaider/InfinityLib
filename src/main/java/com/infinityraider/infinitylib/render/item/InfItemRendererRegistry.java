package com.infinityraider.infinitylib.render.item;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.InfinityLib;
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
public class ItemRendererRegistry {
    private static final ItemRendererRegistry INSTANCE = new ItemRendererRegistry();

    public static ItemRendererRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<Item, InfItemRenderer> registry;
    private final Dispatcher dispatcher;
    private final InfItemRenderer defaultRenderer;

    private ItemRendererRegistry() {
        this.registry = Maps.newIdentityHashMap();
        this.dispatcher = new Dispatcher(this);
        this.defaultRenderer = ItemStackTileEntityRenderer.instance::func_239207_a_;
    }

    public ItemRendererRegistry register(IInfinityItem item) {
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
        return () -> () -> this.dispatcher;
    }

    private static class Dispatcher extends ItemStackTileEntityRenderer {
        private final ItemRendererRegistry registry;

        private Dispatcher(ItemRendererRegistry registry) {
            this.registry = registry;
        }

        @Override
        public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType perspective, MatrixStack transforms,
                                   IRenderTypeBuffer buffer, int light, int overlay) {
            this.registry.getRenderer(stack).render(stack, perspective, transforms, buffer, light, overlay);
        }
    }
}
