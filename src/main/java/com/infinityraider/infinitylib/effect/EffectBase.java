package com.infinityraider.infinitylib.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class EffectBase extends Effect implements IInfinityEffect {
    private final String name;

    protected EffectBase(String name, EffectType type, int liquidColor) {
        super(type, liquidColor);
        this.name = name;
    }

    @Nonnull
    @Override
    public String getInternalName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    protected TextureAtlasSprite getSprite() {
        PotionSpriteUploader potionspriteuploader = Minecraft.getInstance().getPotionSpriteUploader();
        return potionspriteuploader.getSprite(this);
    }

    @OnlyIn(Dist.CLIENT)
    protected void drawInGui(MatrixStack transforms, int x, int y, AbstractGui gui, int width, int height, float alpha) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        TextureAtlasSprite sprite = this.getSprite();
        Minecraft.getInstance().getTextureManager().bindTexture(sprite.getAtlasTexture().getTextureLocation());
        AbstractGui.blit(transforms, x, y, gui.getBlitOffset(), width, height, this.getSprite());
    }
}
