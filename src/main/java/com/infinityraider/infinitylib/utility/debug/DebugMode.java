package com.infinityraider.infinitylib.utility.debug;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Class to aid with debugging
 */
public abstract class DebugMode {
    /**
     *
     * @return
     */
    public abstract String debugName();

    public abstract void debugAction(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ);
}
