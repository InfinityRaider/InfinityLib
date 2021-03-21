package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.InfinityLib;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class WorldHelper {

    public static <T> Optional<T> getBlock(World world, BlockPos pos, Class<T> type) {
        return Optional.ofNullable(world)
                .map(w -> w.getBlockState(pos))
                .map(AbstractBlock.AbstractBlockState::getBlock)
                .filter(b -> type.isAssignableFrom(b.getClass()))
                .map(type::cast);
    }

    public static <T> Optional<T> getTile(World world, BlockPos pos, Class<T> type) {
        return Optional.ofNullable(world)
                .map(w -> w.getTileEntity(pos))
                .filter(te -> type.isAssignableFrom(te.getClass()))
                .map(type::cast);
    }

    public static <T> Optional<T> getCapability(World world, BlockPos pos, Capability<T> capability, Class<T> type) {
        return Optional.ofNullable(world)
                .map(w -> w.getTileEntity(pos))
                .flatMap(tile -> {
                    if(type.isAssignableFrom(tile.getClass())) {
                        return Optional.of(type.cast(tile));
                    } else {
                        return tile.getCapability(capability).map(obj -> obj);
                    }
                });
    }

    public static <T> List<T> collectBlocks(World world, BlockPos min, BlockPos max, Class<T> type) {
        return streamBlocks(world, min, max, type).collect(Collectors.toList());
    }

    public static <T> List<T> collectTiles(World world, BlockPos min, BlockPos max, Class<T> type) {
        return streamTiles(world, min, max, type).collect(Collectors.toList());
    }

    public static <T> List<T> collectCapabilities(World world, BlockPos min, BlockPos max, Capability<T> capability, Class<T> type) {
        return streamCapabilities(world, min, max, capability, type).collect(Collectors.toList());
    }

    public static <T> Stream<T> streamBlocks(World world, BlockPos min, BlockPos max, Class<T> type) {
        return streamRange(world, min, max, (w, pos) -> getBlock(w, pos, type));
    }

    public static <T> Stream<T> streamTiles(World world, BlockPos min, BlockPos max, Class<T> type) {
        return streamRange(world, min, max, (w, pos) -> getTile(w, pos, type));
    }

    public static <T> Stream<T> streamCapabilities(World world, BlockPos min, BlockPos max, Capability<T> capability, Class<T> type) {
        return streamRange(world, min, max, (w, pos) -> getCapability(w, pos, capability, type));
    }

    public static <T> Stream<T> streamRange(World world, BlockPos min, BlockPos max, BiFunction<World, BlockPos, Optional<T>> getter) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(0, 0, 0);
        return IntStream.rangeClosed(min.getX(), max.getX()).mapToObj(
                x -> IntStream.rangeClosed(min.getY(), max.getY()).mapToObj(
                        y -> IntStream.rangeClosed(min.getZ(), max.getZ()).mapToObj(
                                z -> {
                                    mutable.setPos(x, y, z);
                                    return mutable;
                                })).flatMap(stream -> stream)).flatMap(stream -> stream)
                .map(pos -> getter.apply(world, pos))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }
	
	public static <T> List<T> getTileNeighbors(World world, BlockPos pos, Class<T> type) {
		return getTileNeighbors(world, pos, type, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
	}

	public static <T> List<T> getTileNeighbors(World world, BlockPos pos, Class<T> type, Direction... dirs) {
		List<T> neighbours = new ArrayList<>();
		for (Direction dir : dirs) {
			TileEntity te = world.getTileEntity(pos.add(dir.getXOffset(), dir.getYOffset(), dir.getZOffset()));
			if (te != null && type.isAssignableFrom(te.getClass())) {
				neighbours.add(type.cast(te));
			}
		}
		return neighbours;
	}
    
    public static void spawnItemInWorld(World world, BlockPos pos, Collection<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            spawnItemInWorld(world, pos, stack);
        }
    }
    
    public static void spawnItemInWorld(World world, BlockPos pos, ItemStack... stacks) {
        for (ItemStack stack : stacks) {
            spawnItemInWorld(world, pos, stack);
        }
    }
    
    public static void spawnItemInWorld(World world, BlockPos pos, ItemStack stack) {
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
