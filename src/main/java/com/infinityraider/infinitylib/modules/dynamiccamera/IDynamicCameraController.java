package com.infinityraider.infinitylib.modules.dynamiccamera;

import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public interface IDynamicCameraController {
    int getTransitionDuration();

    void onCameraActivated();

    void onObservationStart();

    void onObservationEnd();

    void onCameraDeactivated();

    boolean shouldContinueObserving();

    Vector3d getObserverPosition();

    Vector2f getObserverOrientation();

    void onFieldOfViewChanged(float fov);
}
