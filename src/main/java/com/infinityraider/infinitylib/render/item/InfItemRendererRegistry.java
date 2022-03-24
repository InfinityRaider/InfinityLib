package com.infinityraider.infinitylib.render.item;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class InfItemRendererRegistry {
    private static final InfItemRendererRegistry INSTANCE = new InfItemRendererRegistry();

    public static InfItemRendererRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<Item, InfItemRenderer> registry;
    private final Supplier<InfItemRenderer> defaultRenderer;

    private InfItemRendererRegistry() {
        this.registry = Maps.newIdentityHashMap();
        this.defaultRenderer = () -> Minecraft.getInstance().getItemRenderer().getBlockEntityRenderer()::renderByItem;
    }

    public InfItemRendererRegistry register(IInfinityItem item) {
        // Register model properties
        IClientItemProperties properties = item.getClientItemProperties().get();
        // Register custom renderer
        properties.getModelProperties().forEach(prop -> ItemProperties.register(item.cast(), prop.getId(), prop::getValue));
        InfItemRenderer renderer = properties.getItemRenderer();
        if(renderer != null) {
            this.registry.put(item.cast(), renderer);
        }
        return this;
    }

    private Supplier<InfItemRenderer> getRenderer(ItemStack stack) {
        return () -> this.registry.getOrDefault(stack.getItem(), this.defaultRenderer.get());
    }

    public IItemRenderProperties getItemRenderer() {
        final Dispatcher dispatcher = new Dispatcher(this);
        return new IItemRenderProperties() {
            @Override
            public Font getFont(ItemStack stack) {
                return dispatcher.getFont(stack);
            }

            @Nullable
            @Override
            public HumanoidModel<?> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                return dispatcher.getArmorModel(entity, stack, slot, defaultModel);
            }

            @Nonnull
            @Override
            public Model getBaseArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                return dispatcher.getBaseArmorModel(entity, stack, slot, defaultModel);
            }

            @Override
            public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {
                dispatcher.renderHelmetOverlay(stack, player, width, height, partialTick);
            }

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return dispatcher;
            }
        };
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
                this.renderer = this.registry.getRenderer(stack).get();
            }
            return this.renderer;
        }

        @Override
        public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemTransforms.TransformType perspective,
                                 @Nonnull PoseStack transforms, @Nonnull MultiBufferSource buffer, int light, int overlay) {
            this.getRenderer(stack).render(stack, perspective, transforms, buffer, light, overlay);
        }

        @Nullable
        public Font getFont(ItemStack stack) {
            return this.getRenderer(stack).getFont(stack);
        }

        @Nullable
        public HumanoidModel<?> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
            return this.getRenderer(stack).getArmorModel(entity, stack, slot, defaultModel);
        }

        @Nonnull
        public Model getBaseArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
            return this.getRenderer(stack).getBaseArmorModel(entity, stack, slot, defaultModel);
        }

        public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {
            this.getRenderer(stack).renderHelmetOverlay(stack, player, width, height, partialTick);
        }
    }
}
