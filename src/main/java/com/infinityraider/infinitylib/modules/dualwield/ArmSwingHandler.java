package com.infinityraider.infinitylib.modules.dualwield;

import com.infinityraider.infinitylib.modules.playeranimations.PlayerAnimationManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    private final Map<UUID, Map<InteractionHand, SwingProgress>> swingHandlers;

    private ArmSwingHandler() {
        this.swingHandlers = new HashMap<>();
    }

    public void swingArm(Player player, InteractionHand hand) {
        this.getSwingProgressForPlayerAndHand(player, hand).swingArm();
    }

    public float getSwingProgress(Player player, InteractionHand hand, float partialTick) {
        return this.getSwingProgressForPlayerAndHand(player, hand).getSwingProgress(partialTick);
    }

    public SwingProgress getSwingProgressForPlayerAndHand(Player player, InteractionHand hand) {
        if(!swingHandlers.containsKey(player.getUUID())) {
            SwingProgress progress = new SwingProgress(player, hand);
            Map<InteractionHand, SwingProgress> subMap = new HashMap<>();
            subMap.put(hand, progress);
            swingHandlers.put(player.getUUID(), subMap);
            return progress;
        }
        Map<InteractionHand, SwingProgress> subMap = swingHandlers.get(player.getUUID());
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
            for(Map<InteractionHand, SwingProgress> subMap : this.swingHandlers.values()) {
                subMap.values().forEach(SwingProgress::onUpdate);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerRenderCall(RenderPlayerEvent.Pre event) {
        float left = this.getSwingProgress(event.getPlayer(), InteractionHand.OFF_HAND, event.getPartialTick());
        float right = this.getSwingProgress(event.getPlayer(), InteractionHand.MAIN_HAND, event.getPartialTick());
        PlayerAnimationManager.setSwingProgress(event.getRenderer(), left, right);
    }

    private static class SwingProgress {
        private final Player player;
        private final InteractionHand hand;

        private float swingProgress;
        private float swingProgressPrev;
        private int swingProgressInt;
        private boolean isSwingInProgress;

        private SwingProgress(Player player, InteractionHand hand) {
            this.player = player;
            this.hand = hand;
        }

        public Player getPlayer() {
            return this.player;
        }

        public InteractionHand getHand() {
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
            ItemStack stack = this.getPlayer().getItemInHand(getHand());
            if (stack.getItem().onEntitySwing(stack, this.getPlayer())) {
                return;
            }
            if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
                this.swingProgressInt = -1;
            }
            this.isSwingInProgress = true;
        }

        private int getArmSwingAnimationEnd() {
            return this.getPlayer().hasEffect(MobEffects.DIG_SPEED)
                    ? 6 - (1 + this.getPlayer().getEffect(MobEffects.DIG_SPEED).getAmplifier())
                    : (this.getPlayer().hasEffect(MobEffects.DIG_SLOWDOWN)
                    ? 6 + (1 + this.getPlayer().getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) * 2 : 6);
        }
    }
}
