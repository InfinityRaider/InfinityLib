package com.infinityraider.infinitylib.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.function.BiFunction;

public class TileReference<T extends BlockEntity> {
    private final BlockPos pos;
    private final BiFunction<LevelAccessor, BlockPos, T> getter;

    private Status status;
    private WeakReference<T> ref;

    public TileReference(BlockPos pos, BiFunction<LevelAccessor, BlockPos, T> getter) {
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
    public T getTile(LevelAccessor world) {
        switch (this.getStatus()) {
            case READY: return this.getTileReady(world);
            case UNLOADED: return this.getTileUnloaded(world);
            case REMOVED: return this.getTileRemoved(world);
        }
        return null;    // never reached
    }

    protected T getTileReady(LevelAccessor world) {
        T tile = this.ref.get();
        if (tile == null) {
            this.status = this.checkLoaded(world) ? Status.REMOVED : Status.UNLOADED;
        }
        return tile;
    }

    protected T getTileUnloaded(LevelAccessor world) {
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

    protected T getTileRemoved(LevelAccessor world) {
        return null;
    }

    public boolean checkLoaded(LevelAccessor world) {
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
