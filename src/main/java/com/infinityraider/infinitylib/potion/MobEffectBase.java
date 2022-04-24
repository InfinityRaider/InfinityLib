package com.infinityraider.infinitylib.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class MobEffectBase extends MobEffect implements IInfinityPotionEffect {
    private final String name;

    protected MobEffectBase(String name, MobEffectCategory type, int liquidColor) {
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
        MobEffectTextureManager mobEffectTextures = Minecraft.getInstance().getMobEffectTextures();
        return mobEffectTextures.get(this);
    }
}
