package com.infinityraider.infinitylib.utility.debug;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class DebugModeFeedback extends DebugMode {

    @Override
    public String debugName() {
        return "feedback";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, UseOnContext context) {
        getDebugData(context.getLevel(), context.getClickedPos(), l -> {
            InfinityLib.instance.getLogger().debug(l);
            context.getPlayer().sendMessage(new TextComponent(l), Util.NIL_UUID);
        });
    }

    @Override
    public void debugActionClicked(ItemStack stack, Level world, Player player, InteractionHand hand) {}

    @Override
    public void debugActionEntityClicked(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {}

    /**
     * Gets strings representing the debug information for the
     * provided location.
     *
     * @param world the world object
     * @param pos the block position
     * @param consumer a consumer accepting the lines of debug data.
     */
    private void getDebugData(Level world, BlockPos pos, Consumer<String> consumer) {
        final boolean remote = world.isClientSide();

        final BlockEntity tile = world.getBlockEntity(pos);

        consumer.accept("------------------");
        consumer.accept(remote ? "client" : "server" + " debug info:");
        consumer.accept("------------------");
        
        consumer.accept("Clicked block at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");

        if (tile instanceof IDebuggable) {
            IDebuggable debuggable = (IDebuggable) tile;
            if (remote) {
                debuggable.addClientDebugInfo(consumer);
            } else {
                debuggable.addServerDebugInfo(consumer);            }
        } else {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            consumer.accept("Block: " + block);
            consumer.accept("State: " + state);
        }

        consumer.accept(" ");
    }
}
