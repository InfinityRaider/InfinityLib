package com.infinityraider.infinitylib.block;

import com.google.common.collect.Lists;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public abstract class BlockDynamicTexture<T extends TileEntityDynamicTexture> extends BlockBaseTile<T> {
    public BlockDynamicTexture(String name, Properties properties) {
        super(name, properties);
    }

    @Nonnull
    @Override
    public BlockItemDynamicTexture asItem() {
        return (BlockItemDynamicTexture) super.asItem();
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

    @Nonnull
    @Override
    @Deprecated
    @SuppressWarnings({"deprecation", "unchecked"})
    public final List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
        List<ItemStack> drops = Lists.newArrayList();
        TileEntity tile = context.get(LootParameters.BLOCK_ENTITY);
        if(tile instanceof TileEntityDynamicTexture) {
            BlockItemDynamicTexture item = this.asItem();
            ItemStack stack = new ItemStack(item);
            item.setMaterial(stack, ((TileEntityDynamicTexture) tile).getMaterial());
            drops.add(stack);
            this.addDrops(drops::add, state, (T) tile, context);
        }
        return drops;
    }

    public abstract void addDrops(Consumer<ItemStack> dropAcceptor, BlockState state, T tile, LootContext.Builder context);

    public abstract void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, @Nullable TileEntity tile);
}
