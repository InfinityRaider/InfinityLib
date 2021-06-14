package com.infinityraider.infinitylib.modules.dualwield;

import com.infinityraider.infinitylib.modules.playeranimations.PlayerAnimationManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ArmSwingHandler {
    private static final ArmSwingHandler INSTANCE = new ArmSwingHandler();

    public static ArmSwingHandler getInstance() {
        return INSTANCE;
    }

    private final Map<UUID, Map<Hand, SwingProgress>> swingHandlers;

    private ArmSwingHandler() {
        this.swingHandlers = new HashMap<>();
    }

    public void swingArm(PlayerEntity player, Hand hand) {
        this.getSwingProgressForPlayerAndHand(player, hand).swingArm();
    }

    public float getSwingProgress(PlayerEntity player, Hand hand, float partialTick) {
        return this.getSwingProgressForPlayerAndHand(player, hand).getSwingProgress(partialTick);
    }

    public SwingProgress getSwingProgressForPlayerAndHand(PlayerEntity player, Hand hand) {
        if(!swingHandlers.containsKey(player.getUniqueID())) {
            SwingProgress progress = new SwingProgress(player, hand);
            Map<Hand, SwingProgress> subMap = new HashMap<>();
            subMap.put(hand, progress);
            swingHandlers.put(player.getUniqueID(), subMap);
            return progress;
        }
        Map<Hand, SwingProgress> subMap = swingHandlers.get(player.getUniqueID());
        if(!subMap.containsKey(hand)) {
            SwingProgress progress = new SwingProgress(player, hand);
            subMap.put(hand, progress);
            return progress;
        }
        return subMap.get(hand);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onUpdateTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            for(Map<Hand, SwingProgress> subMap : this.swingHandlers.values()) {
                subMap.values().forEach(SwingProgress::onUpdate);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerRenderCall(RenderPlayerEvent.Pre event) {
        float left = this.getSwingProgress(event.getPlayer(), Hand.OFF_HAND, event.getPartialRenderTick());
        float right = this.getSwingProgress(event.getPlayer(), Hand.MAIN_HAND, event.getPartialRenderTick());
        PlayerAnimationManager.setSwingProgress(event.getRenderer(), left, right);
    }

    private static class SwingProgress {
        private final PlayerEntity player;
        private final Hand hand;

        private float swingProgress;
        private float swingProgressPrev;
        private int swingProgressInt;
        private boolean isSwingInProgress;

        private SwingProgress(PlayerEntity player, Hand hand) {
            this.player = player;
            this.hand = hand;
        }

        public PlayerEntity getPlayer() {
            return this.player;
        }

        public Hand getHand() {
            return this.hand;
        }

        public float getSwingProgress(float partialTick) {
            float f = this.swingProgress - this.swingProgressPrev;
            if (f < 0.0F) {
                ++f;
            }
            return this.swingProgressPrev + f * partialTick;
        }

        private void onUpdate() {
            this.swingProgressPrev = this.swingProgress;
            this.updateArmSwingProgress();
        }

        private void updateArmSwingProgress() {
            int i = this.getArmSwingAnimationEnd();
            if (this.isSwingInProgress) {
                ++this.swingProgressInt;
                if (this.swingProgressInt >= i) {
                    this.swingProgressInt = 0;
                    this.isSwingInProgress = false;
                }
            } else {
                this.swingProgressInt = 0;
            }
            this.swingProgress = (float) this.swingProgressInt / (float) i;
        }

        public void swingArm() {
            ItemStack stack = this.getPlayer().getHeldItem(getHand());
            if (stack != null && stack.getItem().onEntitySwing(stack, this.getPlayer())) {
                return;
            }
            if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
                this.swingProgressInt = -1;
            }
            this.isSwingInProgress = true;
        }

        private int getArmSwingAnimationEnd() {
            return this.getPlayer().isPotionActive(Effects.HASTE)
                    ? 6 - (1 + this.getPlayer().getActivePotionEffect(Effects.HASTE).getAmplifier())
                    : (this.getPlayer().isPotionActive(Effects.MINING_FATIGUE)
                    ? 6 + (1 + this.getPlayer().getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) * 2 : 6);
        }
    }
}
