package com.infinityraider.infinitylib.modules.playeranimations;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Map;

// TODO: Figure this out
@OnlyIn(Dist.CLIENT)
public class ModelPlayerCustomized<T extends LivingEntity> extends PlayerModel<T> implements IAnimatablePlayerModel {
    //private static final ModelPlayerCustomized<Player> MODEL_MAIN =  new ModelPlayerCustomized<Player>(ModelLayers.PLAYER, false);
    //private static final ModelPlayerCustomized<Player> MODEL_SLIM =  new ModelPlayerCustomized<Player>(ModelLayers.PLAYER_SLIM, true);

    private float swingProgressLeft;
    private float swingProgressRight;
    private boolean doArmWobble;

    private ModelPlayerCustomized(ModelPart root, boolean smallArmsIn) {
        super(root, smallArmsIn);
    }

    public void setSwingProgress(float left, float right) {
        this.swingProgressLeft = left;
        this.swingProgressRight = right;
    }

    public void setDoArmWobble(boolean status) {
        this.doArmWobble = status;
    }
    
    

    @Override
    @SuppressWarnings("incomplete-switch")
    public void setupAnim(@Nonnull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Call super for cape animation (because it is private and can not be replicated)
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // Modify the behaviour
        // TODO

    }

    private float getArmAngleSq(float limbSwing) {
        return -65.0F * limbSwing + limbSwing * limbSwing;
    }

    private void handleRightArmPose(T entity) {
        switch(this.rightArmPose) {
            case EMPTY:
                this.rightArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.9424779F;
                this.rightArm.yRot = (-(float)Math.PI / 6F);
                break;
            case ITEM:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - ((float)Math.PI / 10F);
                this.rightArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float)Math.PI;
                this.rightArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.yRot = -0.1F + this.head.yRot;
                this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
                this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
                this.leftArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
        }

    }

    private void handleLeftArmPose(T entity) {
        switch (this.leftArmPose) {
            case EMPTY:
                this.leftArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.9424779F;
                this.leftArm.yRot = ((float) Math.PI / 6F);
                break;
            case ITEM:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - ((float) Math.PI / 10F);
                this.leftArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) Math.PI;
                this.leftArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
                this.leftArm.yRot = 0.1F + this.head.yRot;
                this.rightArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
                this.leftArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, false);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
        }
    }

    private void handleSwingProcess(T entity, float ageInTicks) {
        if(this.swingProgressLeft > 0.0F || this.swingProgressRight > 0.0F) {
            if (this.swingProgressLeft > 0.0F) {
                HumanoidArm handSide = HumanoidArm.LEFT;
                ModelPart arm = this.getArm(handSide);
                float f = this.swingProgressLeft;
                this.body.yRot = Mth.sin(Mth.sqrt(f) * ((float) Math.PI * 2F)) * 0.2F;
                this.body.yRot *= -1.0F;
                this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F;
                this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F;
                this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0F;
                this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F;
                this.rightArm.yRot += this.body.yRot;
                this.leftArm.yRot += this.body.yRot;
                this.leftArm.xRot += this.body.yRot;
                f = 1.0F - this.swingProgressLeft;
                f = f * f;
                f = f * f;
                f = 1.0F - f;
                float f1 = Mth.sin(f * (float) Math.PI);
                float f2 = Mth.sin(this.swingProgressLeft * (float) Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
                arm.xRot = (float) ((double) arm.xRot - ((double) f1 * 1.2D + (double) f2));
                arm.yRot += this.body.yRot * 2.0F;
                arm.zRot += Mth.sin(this.swingProgressLeft * (float) Math.PI) * -0.4F;
            } else {
                HumanoidArm handSide = HumanoidArm.RIGHT;
                ModelPart arm = this.getArm(handSide);
                float f = this.swingProgressRight;
                this.body.yRot = Mth.sin(Mth.sqrt(f) * ((float) Math.PI * 2F)) * 0.2F;
                this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F;
                this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F;
                this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0F;
                this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F;
                this.rightArm.yRot += this.body.yRot;
                this.leftArm.yRot += this.body.yRot;
                this.leftArm.xRot += this.body.yRot;
                f = 1.0F - this.swingProgressRight;
                f = f * f;
                f = f * f;
                f = 1.0F - f;
                float f1 = Mth.sin(f * (float) Math.PI);
                float f2 = Mth.sin(this.swingProgressRight * (float) Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
                arm.xRot = (float) ((double) arm.xRot - ((double) f1 * 1.2D + (double) f2));
                arm.yRot += this.body.yRot * 2.0F;
                arm.zRot += Mth.sin(this.swingProgressRight * (float) Math.PI) * -0.4F;
            }
        } else if (this.attackTime > 0.0F) {
            HumanoidArm handSide = entity.swingingArm == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
            ModelPart arm = this.getArm(handSide);
            float f = this.attackTime;
            this.body.yRot = Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
            if (handSide == HumanoidArm.LEFT) {
                this.body.yRot *= -1.0F;
            }
            this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F;
            this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F;
            this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0F;
            this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F;
            this.rightArm.yRot += this.body.yRot;
            this.leftArm.yRot += this.body.yRot;
            this.leftArm.xRot += this.body.yRot;
            f = 1.0F - this.attackTime;
            f = f * f;
            f = f * f;
            f = 1.0F - f;
            float f1 = Mth.sin(f * (float)Math.PI);
            float f2 = Mth.sin(this.attackTime * (float)Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
            arm.xRot = (float)((double)arm.xRot - ((double)f1 * 1.2D + (double)f2));
            arm.yRot += this.body.yRot * 2.0F;
            arm.zRot += Mth.sin(this.attackTime * (float)Math.PI) * -0.4F;
        }
    }

    /*
    public static void replaceOldModel() {
        EntityRenderer<? extends Player> renderer = getOldRenderer("default");
        if(renderer == null) {
            InfinityLib.instance.getLogger().debug("Failed overriding left arm swing behaviour");
            return;
        }
        Model oldModel = ((LivingEntityRenderer<?,?>) renderer).getModel();
        if(oldModel instanceof ModelPlayerCustomized) {
            return;
        }
        PlayerModel<?> newModel = null;
        for(Field field : LivingEntityRenderer.class.getDeclaredFields()) {
            if(Model.class.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    Object obj = field.get(renderer);
                    if (obj == oldModel) {
                        newModel = MODEL_MAIN;
                        field.set(renderer, newModel);
                        break;
                    }
                } catch (Exception e) {
                    InfinityLib.instance.getLogger().printStackTrace(e);
                }
            }
        }
        if(newModel != null) {
            //replace relevant fields in RenderPlayer
            replaceEntriesInRenderPlayer(renderer, newModel);
            replaceEntriesInRenderPlayer(getOldRenderer("slim"), MODEL_SLIM);
        }
    }
     */

    @SuppressWarnings("unchecked")
    private static EntityRenderer<? extends Player> getOldRenderer(String keyword) {
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        Map<String, EntityRenderer<? extends Player>> skinMap = manager.getSkinMap();
        return skinMap.get(keyword);
    }

    private static void replaceEntriesInRenderPlayer(EntityRenderer<? extends Player> renderer, PlayerModel<?> newModel) {
        if(renderer == null) {
            return;
        }
        //replace relevant fields in PlayerRenderer
        for(Field field : LivingEntityRenderer.class.getDeclaredFields()) {
            if(Model.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    field.set(renderer, newModel);
                } catch (IllegalAccessException e) {
                    InfinityLib.instance.getLogger().printStackTrace(e);
                }
                break;
            }
        }
    }
}
