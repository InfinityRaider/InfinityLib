package com.infinityraider.infinitylib.modules.playeranimations;

import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerAnimationManager {
    @OnlyIn(Dist.CLIENT)
    public static void setSwingProgress(PlayerRenderer renderer, float left, float right) {
        PlayerModel<?> model = renderer.getEntityModel();
        if(model instanceof IAnimatablePlayerModel) {
            ((IAnimatablePlayerModel) model).setSwingProgress(left, right);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void setDoArmWobble(PlayerRenderer renderer, boolean wobble) {
        PlayerModel<?> model = renderer.getEntityModel();
        if(model instanceof IAnimatablePlayerModel) {
            ((IAnimatablePlayerModel) model).setDoArmWobble(wobble);
        }
    }
}
