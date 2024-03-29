package com.infinityraider.infinitylib.modules.dynamiccamera;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * Methods in this interface are only called on the client
 */
public interface IDynamicCameraController {
    /**
     * @return The number of ticks for the camera to move between positions
     */
    int getTransitionDuration();

    /**
     * Callback for when the camera is activated for this controller
     */
    void onCameraActivated();

    /**
     * Callback for when the camera has reached this controller's requested position
     */
    void onObservationStart();

    /**
     * Callback for when the camera stops observing this controller's requested position
     */
    void onObservationEnd();

    /**
     * Callback for when the camera is deactivated for this controller
     */
    void onCameraDeactivated();

    /**
     * Called every tick to check if the camera should keep moving to / observing this controller's requested position
     * @return true to continue observing, false to return to its previous position
     */
    boolean shouldContinueObserving();

    /**
     * @return The required position to place the camera at
     */
    Vec3 getObserverPosition();

    /**
     * @return The required orientation to place the camera in
     */
    Vec2 getObserverOrientation();

    /**
     * Callback for when the field of view changed, can be used to recalculate position or orientation
     */
    void onFieldOfViewChanged(double fov);
}
