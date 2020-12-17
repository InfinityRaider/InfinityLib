package com.infinityraider.infinitylib.modules.dualwield;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class MouseClickHandler {
    private static final MouseClickHandler INSTANCE = new MouseClickHandler();

    private PlayerController playerController;

    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;

    private static final int LMB = 0;
    private static final int RMB = 1;

    private MouseClickHandler() {}

    public static MouseClickHandler getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onLeftClick(InputEvent.RawMouseEvent event) {
        if(Minecraft.getInstance().currentScreen != null) {
            // We do not want to do anything while a GUI is open
            return;
        }
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if(player == null) {
            return;
        }
        ItemStack stack = player.getHeldItemOffhand();
        if(event.getButton() != LMB) {
            return;
        }
        leftButtonPressed = !leftButtonPressed;
        if(stack == null || stack.isEmpty()) {
            return;
        }
        if(stack.getItem() instanceof IDualWieldedWeapon) {
            if(leftButtonPressed) {
                boolean shift = Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown();
                boolean ctrl = Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown();
                IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                attackEntity(weapon, player, stack, true, shift, ctrl, Hand.OFF_HAND);
                weapon.onItemUsed(stack, player, shift, ctrl, Hand.OFF_HAND);
                new MessageMouseButtonPressed(true, shift, ctrl).sendToServer();
                Minecraft.getInstance().player.swingArm(Hand.OFF_HAND);
            }
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onRightClick(InputEvent.RawMouseEvent event) {
        if(Minecraft.getInstance().currentScreen != null) {
            // We do not want to do anything while a GUI is open
            return;
        }
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if(player == null) {
            return;
        }
        ItemStack stack = player.getHeldItemMainhand();
        if(event.getButton() != RMB) {
            return;
        }
        rightButtonPressed = !rightButtonPressed;
        if(stack == null || stack.isEmpty()) {
            return;
        }
        if(stack.getItem() instanceof IDualWieldedWeapon) {
            if(rightButtonPressed) {
                boolean shift = Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown();
                boolean ctrl = Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown();
                IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                attackEntity(weapon, player, stack, false, shift, ctrl, Hand.MAIN_HAND);
                weapon.onItemUsed(stack, player, shift, ctrl, Hand.MAIN_HAND);
                new MessageMouseButtonPressed(false, shift, ctrl).sendToServer();
                Minecraft.getInstance().player.swingArm(Hand.MAIN_HAND);
            }
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    private void attackEntity(IDualWieldedWeapon weapon, ClientPlayerEntity player, ItemStack stack, boolean left, boolean shift, boolean ctrl, Hand hand) {
        if(Minecraft.getInstance().objectMouseOver == null) {
            return;
        }
        Entity target = null;
        RayTraceResult mouseOverObj = Minecraft.getInstance().objectMouseOver;
        if(mouseOverObj instanceof EntityRayTraceResult) {
            target = ((EntityRayTraceResult) mouseOverObj).getEntity();
        }
        if(target != null) {
            if(!weapon.onItemAttack(stack, player, target, shift, ctrl, left ? Hand.OFF_HAND : Hand.MAIN_HAND)) {
                if(this.playerController == null) {
                    this.playerController = Minecraft.getInstance().playerController;
                }
                if(this.playerController != null) {
                    new MessageAttackDualWielded(target, left, shift, ctrl).sendToServer();
                    if(this.playerController.getCurrentGameType() != GameType.SPECTATOR) {
                        ModuleDualWield.getInstance().attackTargetEntityWithCurrentItem(player, target, weapon, stack, hand);
                        player.resetCooldown();
                    }
                }
            }
        }
    }
}
