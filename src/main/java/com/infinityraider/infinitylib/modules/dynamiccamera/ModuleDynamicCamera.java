package com.infinityraider.infinitylib.modules.dynamiccamera;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.utility.UnsafeUtil;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;
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

    private final EntityType<DynamicCamera> cameraEntityType;

    private ModuleDynamicCamera() {
        EntityType<DynamicCamera> type = null;
        try {
            type = UnsafeUtil.getInstance().instantiateObject(CameraEntityType.class);
        } catch (InstantiationException e) {
            InfinityLib.instance.getLogger().error("Could not instantiate dynamic camera");
            InfinityLib.instance.getLogger().printStackTrace(e);
        } finally {
            this.cameraEntityType = type;
        }
    }

    public EntityType<DynamicCamera> getCameraEntityType() {
        return this.cameraEntityType;
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

    private static final class CameraEntityType extends EntityType<DynamicCamera> {
        private static final EntityDimensions SIZE = EntityDimensions.fixed(Constants.UNIT, Constants.UNIT);

        public CameraEntityType(EntityFactory<DynamicCamera> factory, MobCategory category, boolean b1, boolean b2, boolean b3, boolean b4, ImmutableSet<Block> blocks, EntityDimensions size, int i1, int i2) {
            super(factory, category, b1, b2, b3, b4, blocks, size, i1, i2);
        }

        @Override
        public EntityDimensions getDimensions() {
            return SIZE;
        }
    }
}
