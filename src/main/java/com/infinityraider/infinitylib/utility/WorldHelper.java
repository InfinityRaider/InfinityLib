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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.capabilities.Capability;

public class WorldHelper {
    public static <T> Optional<T> getBlock(LevelAccessor world, BlockPos pos, Class<T> type) {
        return Optional.ofNullable(world)
                .map(w -> w.getBlockState(pos))
                .map(BlockBehaviour.BlockStateBase::getBlock)
                .filter(b -> type.isAssignableFrom(b.getClass()))
                .map(type::cast);
    }

    public static <T> Optional<T> getTile(LevelAccessor world, BlockPos pos, Class<T> type) {
        return Optional.ofNullable(world)
                .map(w -> w.getBlockEntity(pos))
                .filter(te -> type.isAssignableFrom(te.getClass()))
                .map(type::cast);
    }

    public static <T> Optional<T> getCapability(LevelAccessor world, BlockPos pos, Capability<T> capability, Class<T> type) {
        return Optional.ofNullable(world)
                .map(w -> w.getBlockEntity(pos))
                .flatMap(tile -> {
                    if(type.isAssignableFrom(tile.getClass())) {
                        return Optional.of(type.cast(tile));
                    } else {
                        return tile.getCapability(capability).map(obj -> obj);
                    }
                });
    }

    public static <T> List<T> collectBlocks(LevelAccessor world, BlockPos min, BlockPos max, Class<T> type) {
        return streamBlocks(world, min, max, type).collect(Collectors.toList());
    }

    public static <T> List<T> collectTiles(LevelAccessor world, BlockPos min, BlockPos max, Class<T> type) {
        return streamTiles(world, min, max, type).collect(Collectors.toList());
    }

    public static <T> List<T> collectCapabilities(LevelAccessor world, BlockPos min, BlockPos max, Capability<T> capability, Class<T> type) {
        return streamCapabilities(world, min, max, capability, type).collect(Collectors.toList());
    }

    public static <T> Stream<T> streamBlocks(LevelAccessor world, BlockPos min, BlockPos max, Class<T> type) {
        return streamRange(world, min, max, (w, pos) -> getBlock(w, pos, type));
    }

    public static <T> Stream<T> streamTiles(LevelAccessor world, BlockPos min, BlockPos max, Class<T> type) {
        return streamRange(world, min, max, (w, pos) -> getTile(w, pos, type));
    }

    public static <T> Stream<T> streamCapabilities(LevelAccessor world, BlockPos min, BlockPos max, Capability<T> capability, Class<T> type) {
        return streamRange(world, min, max, (w, pos) -> getCapability(w, pos, capability, type));
    }

    public static <T> Stream<T> streamRange(LevelAccessor world, BlockPos min, BlockPos max, BiFunction<LevelAccessor, BlockPos, Optional<T>> getter) {
        return streamPositions(min, max)
                .map(pos -> getter.apply(world, pos))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public static Stream<BlockPos> streamPositions(BlockPos min, BlockPos max) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(0, 0, 0);
        return IntStream.rangeClosed(min.getX(), max.getX()).mapToObj(
                x -> IntStream.rangeClosed(min.getY(), max.getY()).mapToObj(
                        y -> IntStream.rangeClosed(min.getZ(), max.getZ()).mapToObj(
                                z -> {
                                    mutable.set(x, y, z);
                                    return mutable;
                                })).flatMap(stream -> stream)).flatMap(stream -> stream);
    }
	
	public static <T> List<T> getTileNeighbors(LevelAccessor world, BlockPos pos, Class<T> type) {
		return getTileNeighbors(world, pos, type, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
	}

	public static <T> List<T> getTileNeighbors(LevelAccessor world, BlockPos pos, Class<T> type, Direction... dirs) {
		List<T> neighbours = new ArrayList<>();
		for (Direction dir : dirs) {
			BlockEntity te = world.getBlockEntity(pos.offset(dir.getStepX(), dir.getStepY(), dir.getStepZ()));
			if (te != null && type.isAssignableFrom(te.getClass())) {
				neighbours.add(type.cast(te));
			}
		}
		return neighbours;
	}
    
    public static void spawnItemInWorld(LevelAccessor world, BlockPos pos, Collection<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            spawnItemInWorld(world, pos, stack);
        }
    }
    
    public static void spawnItemInWorld(LevelAccessor world, BlockPos pos, ItemStack... stacks) {
        for (ItemStack stack : stacks) {
            spawnItemInWorld(world, pos, stack);
        }
    }
    
    public static void spawnItemInWorld(Level world, BlockPos pos, ItemStack stack) {
        if (world != null && pos != null && stack != null && stack.getItem() != null) {
            Block.popResource(world, pos, stack);
        } else {
            // Create StringBuilder
            final StringBuilder sb = new StringBuilder();
            // Optionalize
            final Optional<Level> optWorld = Optional.ofNullable(world);
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
            sb.append("\t- In World: ").append(optWorld.map(w -> w.getLevelData().toString()).orElse("<NULL>")).append("\n");
            sb.append("\t- At Location: ").append(optPos.map(p -> p.toString()).orElse("<NULL>")).append("\n");
            sb.append("\t- ItemStack:\n");
            sb.append("\t\t- Item: ").append(optStack.map(ItemStack::getItem).map(Item::toString).orElse("<NULL>")).append("\n");
            sb.append("\t\t- Amount: ").append(optStack.map(ItemStack::getCount).map(Object::toString).orElse("<NULL>")).append("\n");
            sb.append("\t\t- Tags: ").append(optStack.map(ItemStack::getTag).map(CompoundTag::toString).orElse("<NULL>")).append("\n");
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
