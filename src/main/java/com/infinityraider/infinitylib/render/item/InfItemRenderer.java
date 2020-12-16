package com.infinityraider.infinitylib.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface InfItemRenderer {
    void render(ItemStack stack, ItemCameraTransforms.TransformType perspective, MatrixStack transforms,
                IRenderTypeBuffer buffer, int light, int overlay);
}
