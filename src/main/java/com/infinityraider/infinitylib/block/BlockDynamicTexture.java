package com.infinityraider.infinitylib.block;

import com.google.common.collect.Lists;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;

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
    public final void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockEntity tile = world.getBlockEntity(pos);
        if((!world.isClientSide()) && (stack.getItem() instanceof BlockItemDynamicTexture)) {
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
        BlockEntity tile = context.getParameter(LootContextParams.BLOCK_ENTITY);
        if(tile instanceof TileEntityDynamicTexture) {
            BlockItemDynamicTexture item = this.asItem();
            ItemStack stack = new ItemStack(item);
            item.setMaterial(stack, ((TileEntityDynamicTexture) tile).getMaterial());
            drops.add(stack);
            this.addDrops(drops::add, state, (T) tile, context);
        }
        return drops;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TileEntityDynamicTexture) {
            TileEntityDynamicTexture dynTile = (TileEntityDynamicTexture) tile;
            ItemStack stack = new ItemStack(this.asItem(), 1);
            this.asItem().setMaterial(stack, dynTile.getMaterial());
            return stack;
        }
        return super.getCloneItemStack(state, target, world, pos, player);
    }

    public abstract void addDrops(Consumer<ItemStack> dropAcceptor, BlockState state, T tile, LootContext.Builder context);

    public abstract void onBlockPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, @Nullable BlockEntity tile);
}
