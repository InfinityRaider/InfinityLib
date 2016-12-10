package com.infinityraider.infinitylib.utility.debug;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.relauncher.Side;

public class DebugModeFeedback extends DebugMode {

    @Override
    public String debugName() {
        return "feedback";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        for(String dataLine:getDebugData(world, pos)) {
            InfinityLib.instance.getLogger().debug(dataLine);
            player.addChatComponentMessage(new TextComponentString(dataLine));
        }
    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {}

    @Override
    public void debugActionEntityClicked(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {}

    /**
     * Constructs a list of strings representing the debug information for the
     * provided location.
     *
     * @param world the world object
     * @param pos the block position
     * @return a list of strings representing the requested debug data.
     */
    private List<String> getDebugData(World world, BlockPos pos) {
        final List<String> debugData = new ArrayList<>();

        final Side side = world.isRemote ? Side.CLIENT : Side.SERVER;

        final TileEntity tile = world.getTileEntity(pos);

        debugData.add("------------------");
        debugData.add(side + " debug info:");
        debugData.add("------------------");
        
        debugData.add("Clicked block at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");

        if (tile instanceof IDebuggable) {
            IDebuggable debuggable = (IDebuggable) tile;
            if (side == Side.CLIENT) {
                debuggable.addClientDebugInfo(debugData);
            } else {
                debuggable.addServerDebugInfo(debugData);
            }
        } else {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            debugData.add("Block: " + Block.REGISTRY.getNameForObject(block));
            debugData.add("Meta: " + block.getMetaFromState(state));
        }

        debugData.add(" ");

        return debugData;
    }
}
