package com.infinityraider.infinitylib.effect;

import net.minecraft.client.Minecraft;
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
}
