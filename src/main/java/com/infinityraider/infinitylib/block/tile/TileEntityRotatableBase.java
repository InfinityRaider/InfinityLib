package com.infinityraider.infinitylib.block.tile;

import com.infinityraider.infinitylib.reference.Names;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

public abstract class TileEntityRotatableBase extends TileEntityBase implements IRotatableTile {
    private Direction direction = Direction.NORTH;

    public TileEntityRotatableBase(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    protected final void writeTileNBT(CompoundNBT tag) {
        tag.putByte(Names.NBT.DIRECTION, (byte) this.direction.getHorizontalIndex());
        this.writeRotatableTileNBT(tag);
    }

    @Override
    protected final void readTileNBT(BlockState state, CompoundNBT tag) {
        if (tag.contains(Names.NBT.DIRECTION)) {
            this.setDirection(tag.getByte(Names.NBT.DIRECTION));
        }
        this.readRotatableTileNBT(tag);
    }

    protected abstract void readRotatableTileNBT(CompoundNBT tag);

    protected abstract void writeRotatableTileNBT(CompoundNBT tag);

    @Override
    public final Direction getOrientation() {
        return this.direction;
    }

    @Override
    public final void setOrientation(Direction facing) {
        this.direction = (facing != null && facing.getAxis().isHorizontal()) ? facing : this.direction;
    }

    @Override
    public final void incrementRotation(int amount) {
        this.setDirection(this.getOrientation().getHorizontalIndex() + amount);
    }

    // Notice, the ordinal follows the backwards orientation.
    private void setDirection(int cardinal) {
        // EnumFacing can actually handle all this stuff!
        this.setOrientation(Direction.byHorizontalIndex(cardinal));
    }
}
