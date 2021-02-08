package com.infinityraider.infinitylib.modules.dynamiccamera;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public interface IDynamicCameraController {
    int getTransitionDuration();

    void setObserving(PlayerEntity player, boolean status);

    boolean continueObserving();

    Vector3d getObserverPosition();

    Vector2f getObserverOrientation();
}
