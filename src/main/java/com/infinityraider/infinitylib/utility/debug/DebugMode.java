package com.infinityraider.infinitylib.utility.debug;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Class to aid with debugging
 */
public abstract class DebugMode {

    public abstract String debugName();

    public abstract void debugActionBlockClicked(ItemStack stack, ItemUseContext context);

    public abstract void debugActionClicked(ItemStack stack, World world, PlayerEntity player, Hand hand);

    public abstract void debugActionEntityClicked(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand);

}
