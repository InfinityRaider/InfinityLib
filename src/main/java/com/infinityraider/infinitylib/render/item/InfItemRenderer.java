package com.infinityraider.infinitylib.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public interface InfItemRenderer {
    void render(ItemStack stack, ItemTransforms.TransformType perspective, PoseStack transforms,
                MultiBufferSource buffer, int light, int overlay);

    @Nullable
    default Font getFont(ItemStack stack)
    {
        return null;
    }

    @Nullable
    default HumanoidModel<?> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
        return null;
    }

    @Nonnull
    default Model getBaseArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
        return IItemRenderProperties.DUMMY.getBaseArmorModel(entity, stack, slot, defaultModel);
    }

    default void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {}
}
