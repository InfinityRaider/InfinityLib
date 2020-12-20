package com.infinityraider.infinitylib.block.tile;

import com.infinityraider.infinitylib.block.multiblock.IMultiBlockComponent;
import com.infinityraider.infinitylib.block.multiblock.IMultiBlockManager;
import com.infinityraider.infinitylib.block.multiblock.IMultiBlockPartData;
import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public abstract class TileEntityMultiBase<M extends IMultiBlockManager<D>, D extends IMultiBlockPartData> extends TileEntityBase implements IMultiBlockComponent<M, D> {
    public TileEntityMultiBase(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    protected final void writeTileNBT(CompoundNBT tag) {
        if (tag.contains(Names.NBT.MULTI)) {
            CompoundNBT multiBlockTag = tag.getCompound(Names.NBT.MULTI);
            ((IMultiBlockComponent) this).getMultiBlockData().readFromNBT(multiBlockTag);
        }
        this.writeMultiTileNBT(tag);
    }

    @Override
    protected final void readTileNBT(BlockState state, CompoundNBT tag) {
        if (tag.contains(Names.NBT.MULTI)) {
            CompoundNBT multiBlockTag = tag.getCompound(Names.NBT.MULTI);
            ((IMultiBlockComponent) this).getMultiBlockData().readFromNBT(multiBlockTag);
        }
        this.readMultiTileNBT(state, tag);
    }

    protected abstract void writeMultiTileNBT(CompoundNBT tag);

    protected abstract void readMultiTileNBT(BlockState state, CompoundNBT tag);
}
