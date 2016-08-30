package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.blockstate.BlockStateSpecial;
import com.infinityraider.infinitylib.block.blockstate.IBlockStateSpecial;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public abstract class BlockCustomRenderedBase extends BlockBase implements ICustomRenderedBlock {
    public BlockCustomRenderedBase(String name, Material blockMaterial) {
        super(name, blockMaterial);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final IBlockStateSpecial<?, ? extends IBlockState> getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new BlockStateSpecial<>(state, pos, null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<ResourceLocation> getTextures() {
        return Collections.emptyList();
    }
}
