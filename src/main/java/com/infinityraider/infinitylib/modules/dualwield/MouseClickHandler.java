package com.infinityraider.infinitylib.modules.dualwield;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class MouseClickHandler {
    private static final MouseClickHandler INSTANCE = new MouseClickHandler();

    private MultiPlayerGameMode playerController;

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
        if(Minecraft.getInstance().screen != null) {
            // We do not want to do anything while a GUI is open
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) {
            return;
        }
        ItemStack stack = player.getOffhandItem();
        if(event.getButton() != LMB) {
            return;
        }
        leftButtonPressed = !leftButtonPressed;
        if(stack.isEmpty()) {
            return;
        }
        if(stack.getItem() instanceof IDualWieldedWeapon) {
            if(leftButtonPressed) {
                boolean shift = Minecraft.getInstance().options.keyShift.isDown();
                boolean ctrl = Minecraft.getInstance().options.keySprint.isDown();
                IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                attackEntity(weapon, player, stack, true, shift, ctrl, InteractionHand.OFF_HAND);
                weapon.onItemUsed(stack, player, shift, ctrl, InteractionHand.OFF_HAND);
                new MessageMouseButtonPressed(true, shift, ctrl).sendToServer();
                Minecraft.getInstance().player.swing(InteractionHand.OFF_HAND);
            }
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onRightClick(InputEvent.RawMouseEvent event) {
        if(Minecraft.getInstance().screen != null) {
            // We do not want to do anything while a GUI is open
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if(event.getButton() != RMB) {
            return;
        }
        rightButtonPressed = !rightButtonPressed;
        if(stack.isEmpty()) {
            return;
        }
        if(stack.getItem() instanceof IDualWieldedWeapon) {
            if(rightButtonPressed) {
                boolean shift = Minecraft.getInstance().options.keyShift.isDown();
                boolean ctrl = Minecraft.getInstance().options.keySprint.isDown();
                IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                attackEntity(weapon, player, stack, false, shift, ctrl, InteractionHand.MAIN_HAND);
                weapon.onItemUsed(stack, player, shift, ctrl, InteractionHand.MAIN_HAND);
                new MessageMouseButtonPressed(false, shift, ctrl).sendToServer();
                Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
            }
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    private void attackEntity(IDualWieldedWeapon weapon, LocalPlayer player, ItemStack stack, boolean left, boolean shift, boolean ctrl, InteractionHand hand) {
        if(Minecraft.getInstance().hitResult == null) {
            return;
        }
        Entity target = null;
        HitResult mouseOverObj = Minecraft.getInstance().hitResult;
        if(mouseOverObj instanceof EntityHitResult) {
            target = ((EntityHitResult) mouseOverObj).getEntity();
        }
        if(target != null) {
            if(!weapon.onItemAttack(stack, player, target, shift, ctrl, left ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND)) {
                if(this.playerController == null) {
                    this.playerController = Minecraft.getInstance().gameMode;
                }
                if(this.playerController != null) {
                    new MessageAttackDualWielded(target, left, shift, ctrl).sendToServer();
                    if(this.playerController.getPlayerMode() != GameType.SPECTATOR) {
                        ModuleDualWield.getInstance().attackTargetEntityWithCurrentItem(player, target, weapon, stack, hand);
                        player.resetAttackStrengthTicker();
                    }
                }
            }
        }
    }
}
