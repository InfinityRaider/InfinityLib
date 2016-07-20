package com.infinityraider.infinitylib.block.tile;

import com.infinityraider.infinitylib.block.multiblock.IMultiBlockComponent;
import com.infinityraider.infinitylib.block.multiblock.IMultiBlockManager;
import com.infinityraider.infinitylib.block.multiblock.IMultiBlockPartData;
import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityMultiBase<M extends IMultiBlockManager<D>, D extends IMultiBlockPartData> extends TileEntityBase implements IMultiBlockComponent<M, D> {
    @Override
    public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagCompound multiBlockTag = new NBTTagCompound();
        ((IMultiBlockComponent) this).getMultiBlockData().writeToNBT(multiBlockTag);
        tag.setTag(Names.NBT.MULTI, multiBlockTag);
        this.writeTileNBT(tag);
        return tag;
    }

    protected abstract void writeTileNBT(NBTTagCompound tag);

    /**
     * Reads the tile entity from an NBTTag.
     */
    @Override
    public final void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(Names.NBT.MULTI)) {
            NBTTagCompound multiBlockTag = tag.getCompoundTag(Names.NBT.MULTI);
            ((IMultiBlockComponent) this).getMultiBlockData().readFromNBT(multiBlockTag);
        }
        this.readTileNBT(tag);
    }

    protected abstract void readTileNBT(NBTTagCompound tag);
}
