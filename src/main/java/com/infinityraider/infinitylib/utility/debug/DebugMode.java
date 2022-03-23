package com.infinityraider.infinitylib.utility.debug;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Class to aid with debugging
 */
public abstract class DebugMode {

    public abstract String debugName();

    public abstract void debugActionBlockClicked(ItemStack stack, UseOnContext context);

    public abstract void debugActionClicked(ItemStack stack, Level world, Player player, InteractionHand hand);

    public abstract void debugActionEntityClicked(ItemStack stack, Player player, LivingEntity target, InteractionHand hand);

}
