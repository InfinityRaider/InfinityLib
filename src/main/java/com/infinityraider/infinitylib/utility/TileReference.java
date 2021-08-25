package com.infinityraider.infinitylib.utility;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.function.BiFunction;

public class TileReference<T extends TileEntity> {
    private final BlockPos pos;
    private final BiFunction<IWorldReader, BlockPos, T> getter;

    private Status status;
    private WeakReference<T> ref;

    public TileReference(BlockPos pos, BiFunction<IWorldReader, BlockPos, T> getter) {
        this.pos = pos;
        this.getter = getter;
        this.status = Status.UNLOADED;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Status getStatus() {
        return this.status;
    }

    public boolean isReady() {
        return this.getStatus().isReady();
    }

    public boolean isUnloaded() {
        return this.getStatus().isUnloaded();
    }

    public boolean isRemoved() {
        return this.getStatus().isRemoved();
    }

    @Nullable
    public T getTile(IWorldReader world) {
        switch (this.getStatus()) {
            case READY: return this.getTileReady(world);
            case UNLOADED: return this.getTileUnloaded(world);
            case REMOVED: return this.getTileRemoved(world);
        }
        return null;    // never reached
    }

    protected T getTileReady(IWorldReader world) {
        T tile = this.ref.get();
        if (tile == null) {
            this.status = this.checkLoaded(world) ? Status.REMOVED : Status.UNLOADED;
        }
        return tile;
    }

    protected T getTileUnloaded(IWorldReader world) {
        if(this.checkLoaded(world)) {
            T tile = this.getter.apply(world, this.getPos());
            if(tile == null) {
                this.status = Status.REMOVED;
            } else {
                this.ref = new WeakReference<>(tile);
                this.status = Status.READY;
            }
            return tile;
        } else {
            return null;
        }
    }

    protected T getTileRemoved(IWorldReader world) {
        return null;
    }

    public boolean checkLoaded(IWorldReader world) {
        return world.isAreaLoaded(this.getPos(), 0);
    }

    public enum Status {
        READY(),
        UNLOADED(),
        REMOVED();

        Status() {}

        public boolean isReady() {
            return this == READY;
        }

        public boolean isUnloaded() {
            return this == UNLOADED;
        }

        public boolean isRemoved() {
            return this == REMOVED;
        }
    }
}
