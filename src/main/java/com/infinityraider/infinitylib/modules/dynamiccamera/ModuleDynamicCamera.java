package com.infinityraider.infinitylib.modules.dynamiccamera;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.modules.Module;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module allowing dynamic control of Minecraft's camera.
 */
@OnlyIn(Dist.CLIENT)
public class ModuleDynamicCamera extends Module {
    private static final ModuleDynamicCamera INSTANCE = new ModuleDynamicCamera();

    public static ModuleDynamicCamera getInstance() {
        return INSTANCE;
    }

    private ModuleDynamicCamera() {}

    public List<Object> getClientEventHandlers() {
        return ImmutableList.of(this);
    }

    public void startObserving(IDynamicCameraController controller) {
        DynamicCamera.startControllingCamera(controller);
    }

    public void stopObserving() {
        DynamicCamera.stopControllingCamera();
    }

    public boolean toggleObserving(IDynamicCameraController controller, boolean status) {
        return DynamicCamera.toggleCameraControl(controller, status);
    }

    @Nullable
    public IDynamicCameraController getCameraController() {
        return DynamicCamera.getCameraController();
    }

    public DynamicCamera.Status getCameraStatus() {
        return DynamicCamera.getCameraStatus();
    }

    public int getCameraAnimationFrame() {
        return DynamicCamera.getCameraAnimationFrame();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.START) {
            return;
        }
        DynamicCamera.tickCamera();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onWorldUnloaded(WorldEvent.Unload event) {
        DynamicCamera.resetCamera();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderHand(RenderHandEvent event) {
        if(DynamicCamera.isCameraActive()) {
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(DynamicCamera.isCameraActive()) {
            if(event.getKey() == GLFW.GLFW_KEY_ESCAPE) {
                this.stopObserving();
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFieldOfViewUpdate(FOVUpdateEvent event) {
        DynamicCamera.onFieldOfViewUpdate(event.getNewfov());
    }
}
