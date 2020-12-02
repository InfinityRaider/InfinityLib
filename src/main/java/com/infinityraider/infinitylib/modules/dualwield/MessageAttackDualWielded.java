package com.infinityraider.infinitylib.modules.dualwield;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

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
        ServerPlayerEntity player = ctx.getSender();
        if(player != null) {
            ItemStack stack = player.getHeldItem(left ? Hand.OFF_HAND : Hand.MAIN_HAND);
            if(stack != null && stack.getItem() instanceof IDualWieldedWeapon) {
                IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                Hand hand = left ? Hand.OFF_HAND : Hand.MAIN_HAND;
                weapon.onItemAttack(stack, player, entity, shift, ctrl, hand);
                ModuleDualWield.getInstance().attackTargetEntityWithCurrentItem(player, entity, weapon, stack, hand);
            }
        }
    }
}
