package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class NBTHelper {
    public static CompoundTag writeBoolArray(String key, CompoundTag tag, boolean[] array) {
        int[] ints = new int[array.length];
        for(int i = 0; i < array.length; i++) {
            ints[i] = array[i] ? 1 : 0;
        }
        tag.putIntArray(key, ints);
        return tag;
    }

    public static boolean[] readBoolArray(String key, CompoundTag tag) {
        int[] ints = tag.getIntArray(key);
        boolean[] array = new boolean[ints.length];
        for(int i = 0; i < ints.length; i++) {
            array[i] = ints[i] > 0;
        }
        return array;
    }

    public static final void addCoordsToNBT(int[] coords, CompoundTag tag) {
        if (coords != null && coords.length == 3) {
            addCoordsToNBT(coords[0], coords[1], coords[2], tag);
        }
    }

    public static final void addCoordsToNBT(int x, int y, int z, CompoundTag tag) {
        tag.putInt(Names.NBT.X, x);
        tag.putInt(Names.NBT.Y, y);
        tag.putInt(Names.NBT.Z, z);
    }

    public static final int[] getCoordsFromNBT(CompoundTag tag) {
        int[] coords = null;
        if (tag.contains(Names.NBT.X) && tag.contains(Names.NBT.Y) && tag.contains(Names.NBT.Z)) {
            coords = new int[]{tag.getInt(Names.NBT.X), tag.getInt(Names.NBT.Y), tag.getInt(Names.NBT.Z)};
        }
        return coords;
    }

    public static final boolean hasKey(CompoundTag tag, String... keys) {
        if (tag == null) {
            return false;
        }
        for (String key : keys) {
            if (!tag.contains(key)) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public static final CompoundTag asTag(Object obj) {
        if (obj instanceof ItemStack) {
            return ((ItemStack) obj).getTag();
        } else if (obj instanceof CompoundTag) {
            return (CompoundTag) obj;
        } else {
            return null;
        }
    }
}
