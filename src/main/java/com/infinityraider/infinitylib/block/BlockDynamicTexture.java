package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class BlockDynamicTexture<T extends TileEntityDynamicTexture> extends BlockBaseTile<T> {
    public BlockDynamicTexture(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public final void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if((!world.isRemote()) && (stack.getItem() instanceof BlockItemDynamicTexture)) {
            if(tile instanceof TileEntityDynamicTexture) {
                TileEntityDynamicTexture dynTile = (TileEntityDynamicTexture) tile;
                dynTile.setMaterial(((BlockItemDynamicTexture) stack.getItem()).getMaterial(stack));
            }
        }
        this.onBlockPlacedBy(world, pos, state, placer, stack, tile);
    }

    public abstract void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, @Nullable TileEntity tile);
}
