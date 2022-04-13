package com.infinityraider.infinitylib.modules.dynamiccamera;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.entity.EntityTypeBase;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.reference.Names;
import com.infinityraider.infinitylib.utility.registration.InfinityLibContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
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

    private RegistryInitializer<EntityTypeBase<DynamicCamera>> cameraEntityType;

    private ModuleDynamicCamera() {

    }

    public EntityType<DynamicCamera> getCameraEntityType() {
        return this.cameraEntityType.get();
    }

    @Override
    public void initRegistrables(InfinityLibContentRegistry registry) {
        this.cameraEntityType = registry.registerEntity(() -> EntityTypeBase.entityTypeBuilder(
                Names.Entities.CAMERA, DynamicCamera.class, DynamicCamera.SpawnFactory.getInstance(), MobCategory.MISC,
                EntityDimensions.fixed(Constants.UNIT, Constants.UNIT))
                .build()
        );
    }

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
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if(DynamicCamera.isCameraInPlayer(InfinityLib.instance.getClientPlayer(), event.getPartialTick())) {
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
    public void onFieldOfViewUpdate(EntityViewRenderEvent.FieldOfView event) {
        DynamicCamera.onFieldOfViewUpdate(event.getFOV());
    }
}
