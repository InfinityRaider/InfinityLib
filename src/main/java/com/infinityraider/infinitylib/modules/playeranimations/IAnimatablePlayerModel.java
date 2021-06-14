package com.infinityraider.infinitylib.modules.playeranimations;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IAnimatablePlayerModel {
    void setSwingProgress(float left, float right);

    void setDoArmWobble(boolean wobble);
}
