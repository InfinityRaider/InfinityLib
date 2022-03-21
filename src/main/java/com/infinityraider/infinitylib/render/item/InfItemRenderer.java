package com.infinityraider.infinitylib.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface InfItemRenderer {
    void render(ItemStack stack, ItemTransforms.TransformType perspective, PoseStack transforms,
                MultiBufferSource buffer, int light, int overlay);
}
