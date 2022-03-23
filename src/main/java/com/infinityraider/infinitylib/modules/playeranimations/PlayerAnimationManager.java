package com.infinityraider.infinitylib.modules.playeranimations;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerAnimationManager {
    @OnlyIn(Dist.CLIENT)
    public static void setSwingProgress(PlayerRenderer renderer, float left, float right) {
        PlayerModel<?> model = renderer.getModel();
        if(model instanceof IAnimatablePlayerModel) {
            ((IAnimatablePlayerModel) model).setSwingProgress(left, right);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void setDoArmWobble(PlayerRenderer renderer, boolean wobble) {
        PlayerModel<?> model = renderer.getModel();
        if(model instanceof IAnimatablePlayerModel) {
            ((IAnimatablePlayerModel) model).setDoArmWobble(wobble);
        }
    }
}
