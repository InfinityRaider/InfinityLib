package com.infinityraider.infinitylib.utility.debug;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class DebugModeFeedback extends DebugMode {

    @Override
    public String debugName() {
        return "feedback";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        getDebugData(context.getWorld(), context.getPos(), l -> {
            InfinityLib.instance.getLogger().debug(l);
            context.getPlayer().sendMessage(new StringTextComponent(l), Util.DUMMY_UUID);
        });
    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, PlayerEntity player, Hand hand) {}

    @Override
    public void debugActionEntityClicked(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {}

    /**
     * Gets strings representing the debug information for the
     * provided location.
     *
     * @param world the world object
     * @param pos the block position
     * @param consumer a consumer accepting the lines of debug data.
     */
    private void getDebugData(World world, BlockPos pos, Consumer<String> consumer) {
        final boolean remote = world.isRemote;

        final TileEntity tile = world.getTileEntity(pos);

        consumer.accept("------------------");
        consumer.accept(remote ? "server" : "client" + " debug info:");
        consumer.accept("------------------");
        
        consumer.accept("Clicked block at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");

        if (tile instanceof IDebuggable) {
            IDebuggable debuggable = (IDebuggable) tile;
            if (remote) {
                debuggable.addServerDebugInfo(consumer);
            } else {
                debuggable.addClientDebugInfo(consumer);
            }
        } else {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            consumer.accept("Block: " + block);
            consumer.accept("State: " + state);
        }

        consumer.accept(" ");
    }
}
