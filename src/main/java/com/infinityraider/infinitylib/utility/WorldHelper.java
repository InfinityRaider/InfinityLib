/*
 */
package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.InfinityLib;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldHelper {
	
	public static final <T> Optional<T> getBlock(World world, BlockPos pos, Class<T> type) {
        return Optional.ofNullable(world)
                .map(w -> w.getBlockState(pos))
                .map(s -> s.getBlock())
                .filter(b -> type.isAssignableFrom(b.getClass()))
                .map(b -> type.cast(b));
	}
	
	public static final <T> Optional<T> getTile(World world, BlockPos pos, Class<T> type) {
        return Optional.ofNullable(world)
                .map(w -> w.getTileEntity(pos))
                .filter(te -> type.isAssignableFrom(te.getClass()))
                .map(te -> type.cast(te));
	}
	
	public static final <T> List<T> getTileNeighbors(World world, BlockPos pos, Class<T> type) {
		return getTileNeighbors(world, pos, type, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
	}

	public static final <T> List<T> getTileNeighbors(World world, BlockPos pos, Class<T> type, Direction... dirs) {
		List<T> neighbours = new ArrayList<>();
		for (Direction dir : dirs) {
			TileEntity te = world.getTileEntity(pos.add(dir.getXOffset(), dir.getYOffset(), dir.getZOffset()));
			if (te != null && type.isAssignableFrom(te.getClass())) {
				neighbours.add(type.cast(te));
			}
		}
		return neighbours;
	}
    
    public static final void spawnItemInWorld(World world, BlockPos pos, Collection<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            spawnItemInWorld(world, pos, stack);
        }
    }
    
    public static final void spawnItemInWorld(World world, BlockPos pos, ItemStack... stacks) {
        for (ItemStack stack : stacks) {
            spawnItemInWorld(world, pos, stack);
        }
    }
    
    public static final void spawnItemInWorld(World world, BlockPos pos, ItemStack stack) {
        if (world != null && pos != null && stack != null && stack.getItem() != null) {
            Block.spawnAsEntity(world, pos, stack);
        } else {
            // Create StringBuilder
            final StringBuilder sb = new StringBuilder();
            // Optionalize
            final Optional<World> optWorld = Optional.ofNullable(world);
            final Optional<BlockPos> optPos = Optional.ofNullable(pos);
            final Optional<ItemStack> optStack = Optional.ofNullable(stack);
            // Append Information
            sb.append("\n");
            sb.append("==================================================\n");
            sb.append("InfinityLib Warning!\n");
            sb.append("==================================================\n");
            sb.append("\n");
            sb.append("Cause:\n");
            sb.append("--------------------------------------------------\n");
            sb.append("\tAn attempt at spawning a bad ItemStack in the world was intercepted!\n");
            sb.append("\n");
            sb.append("Relevant Data:\n");
            sb.append("--------------------------------------------------\n");
            sb.append("\t- In World: ").append(optWorld.map(w -> w.getWorldInfo().toString()).orElse("<NULL>")).append("\n");
            sb.append("\t- At Location: ").append(optPos.map(p -> p.toString()).orElse("<NULL>")).append("\n");
            sb.append("\t- ItemStack:\n");
            sb.append("\t\t- Item: ").append(optStack.map(ItemStack::getItem).map(Item::toString).orElse("<NULL>")).append("\n");
            sb.append("\t\t- Amount: ").append(optStack.map(ItemStack::getCount).map(Object::toString).orElse("<NULL>")).append("\n");
            sb.append("\t\t- Tags: ").append(optStack.map(ItemStack::getTag).map(CompoundNBT::toString).orElse("<NULL>")).append("\n");
            // Append Stack Trace
            sb.append("\n");
            sb.append("Stacktrace:\n");
            sb.append("--------------------------------------------------\n");
            for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
                sb.append("\t").append(e.toString()).append("\n");
            }
            // End Message
            sb.append("==================================================\n");
            // Send Information to Log
            InfinityLib.instance.getLogger().error(sb.toString());
        }
    }

}
