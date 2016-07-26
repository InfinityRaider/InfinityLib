package com.infinityraider.infinitylib.block.tile;

import com.infinityraider.infinitylib.reference.Names;
import com.infinityraider.infinitylib.utility.math.Directions;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public abstract class TileEntityRotatableBase extends TileEntityBase implements IRotatableTile {
    private Directions.Direction orientation = Directions.Direction.UNKNOWN;

    @Override
    public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.orientation == null) {
            this.orientation = Directions.Direction.UNKNOWN;
        }
        tag.setByte(Names.NBT.DIRECTION, (byte) this.orientation.ordinal());
        this.writeTileNBT(tag);
        return tag;
    }

    protected abstract void writeTileNBT(NBTTagCompound tag);
    @Override

    public final void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(Names.NBT.DIRECTION)) {
            this.setDirection(tag.getByte(Names.NBT.DIRECTION));
        }
        this.readTileNBT(tag);
    }

    protected abstract void readTileNBT(NBTTagCompound tag);

    public final EnumFacing getOrientation() {
        return this.orientation.getEnumFacing();
    }

    public final void setOrientation(EnumFacing facing) {
        this.setDirection(Directions.Direction.getFromEnumFacing(facing));
    }

    public final Directions.Direction getDirection() {
        return orientation;
    }

    public final void setDirection(Directions.Direction orientation) {
        if (orientation != Directions.Direction.UNKNOWN) {
            this.orientation = orientation;
            if (this.worldObj != null && !this.worldObj.isRemote) {
                this.markForUpdate();
            }
        }
    }
    private void setDirection(int orientation) {
        this.setDirection(Directions.Direction.getOrientation(orientation));
    }
}
