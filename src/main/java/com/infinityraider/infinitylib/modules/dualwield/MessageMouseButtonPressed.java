package com.infinityraider.infinitylib.modules.dualwield;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageMouseButtonPressed extends MessageBase {
    private boolean left;
    private boolean shift;
    private boolean ctrl;

    public MessageMouseButtonPressed() {}

    public MessageMouseButtonPressed(boolean left, boolean shift, boolean ctrl) {
        this();
        this.left = left;
        this.shift = shift;
        this.ctrl = ctrl;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        ItemStack stack = player.getHeldItem(left ? Hand.OFF_HAND : Hand.MAIN_HAND);
        // Forward item use to the stack
        if (stack != null && stack.getItem() instanceof IDualWieldedWeapon) {
            IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
            weapon.onItemUsed(stack, player, shift, ctrl, left ? Hand.OFF_HAND : Hand.MAIN_HAND);
        }
        // Notify all clients of the item usage (for animation)
        new MessageSwingArm(player, left ? Hand.OFF_HAND : Hand.MAIN_HAND).sendToAll();

    }
}
