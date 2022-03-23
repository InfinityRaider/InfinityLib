package com.infinityraider.infinitylib.modules.dualwield;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageAttackDualWielded extends MessageBase {
    private boolean left;
    private boolean shift;
    private boolean ctrl;
    private Entity entity;

    public MessageAttackDualWielded() {
        super();
    }

    public MessageAttackDualWielded(Entity entity, boolean left, boolean shift, boolean ctrl) {
        this();
        this.left = left;
        this.shift = shift;
        this.ctrl = ctrl;
        this.entity = entity;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if(player != null) {
            ItemStack stack = player.getItemInHand(left ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if(stack != null && stack.getItem() instanceof IDualWieldedWeapon) {
                IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                InteractionHand hand = left ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                weapon.onItemAttack(stack, player, entity, shift, ctrl, hand);
                ModuleDualWield.getInstance().attackTargetEntityWithCurrentItem(player, entity, weapon, stack, hand);
            }
        }
    }
}
