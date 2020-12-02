package com.infinityraider.infinitylib.block.multiblock;

import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.nbt.CompoundNBT;

public class MultiBlockPartData implements IMultiBlockPartData {
    private int posX;
    private int posY;
    private int posZ;
    private int sizeX;
    private int sizeY;
    private int sizeZ;

    public MultiBlockPartData(int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    @Override
    public int posX() {
        return posX;
    }

    @Override
    public int posY() {
        return posY;
    }

    @Override
    public int posZ() {
        return posZ;
    }

    @Override
    public int sizeX() {
        return sizeX;
    }

    @Override
    public int sizeY() {
        return sizeY;
    }

    @Override
    public int sizeZ() {
        return sizeZ;
    }

    @Override
    public void writeToNBT(CompoundNBT tag) {
        tag.putInt(Names.NBT.X1, posX());
        tag.putInt(Names.NBT.Y1, posY());
        tag.putInt(Names.NBT.Z1, posZ());
        tag.putInt(Names.NBT.X2, sizeX());
        tag.putInt(Names.NBT.Y2, sizeY());
        tag.putInt(Names.NBT.Z2, sizeZ());
    }

    @Override
    public void readFromNBT(CompoundNBT tag) {
        this.posX = tag.getInt(Names.NBT.X1);
        this.posY = tag.getInt(Names.NBT.Y1);
        this.posZ = tag.getInt(Names.NBT.Z1);
        this.sizeX = tag.getInt(Names.NBT.X2);
        this.sizeY = tag.getInt(Names.NBT.Y2);
        this.sizeZ = tag.getInt(Names.NBT.Z2);
    }

    @Override
    public String toString() {
        return String.format("(x: %1$d / %2$d, y: %3$d / %4$d, z: %5$d / %6$d)", posX() + 1, sizeX(), posY() + 1, sizeY(), posZ() + 1, sizeZ());
    }
}
