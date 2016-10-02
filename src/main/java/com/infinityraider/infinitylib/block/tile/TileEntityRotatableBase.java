package com.infinityraider.infinitylib.block.tile;

import com.infinityraider.infinitylib.reference.Names;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public abstract class TileEntityRotatableBase extends TileEntityBase implements IRotatableTile {
    public static EnumFacing[] VALID_DIRECTIONS = {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};

    @Nonnull
    private EnumFacing direction = EnumFacing.NORTH;

    @Override
    protected final void writeTileNBT(NBTTagCompound tag) {
        tag.setByte(Names.NBT.DIRECTION, (byte) this.direction.ordinal());
        this.writeRotatableTileNBT(tag);
    }

    @Override
    protected final void readTileNBT(NBTTagCompound tag) {
        if (tag.hasKey(Names.NBT.DIRECTION)) {
            this.setDirection(tag.getByte(Names.NBT.DIRECTION));
        }
        this.readRotatableTileNBT(tag);
    }

    protected abstract void readRotatableTileNBT(NBTTagCompound tag);

    protected abstract void writeRotatableTileNBT(NBTTagCompound tag);

    @Override
    public final EnumFacing getOrientation() {
        return this.direction;
    }

    @Override
    public final void setOrientation(EnumFacing facing) {
        this.direction = facing == null ? this.direction : facing ;
    }

    @Override
    public final void incrementRotation(int amount) {
        if(!worldObj.isRemote) {
            return;
        }
        int index = 0;
        for(int i = 0; i < VALID_DIRECTIONS.length; i++) {
            if(VALID_DIRECTIONS[i] == this.getOrientation()) {
                index = i;
                break;
            }
        }
        this.setOrientation(VALID_DIRECTIONS[Math.max((index + amount) % VALID_DIRECTIONS.length, 0)]);
    }

    private void setDirection(int orientation) {
        this.setOrientation(EnumFacing.values()[Math.abs(orientation) % EnumFacing.values().length]);
    }
}
