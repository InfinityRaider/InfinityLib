package com.infinityraider.infinitylib.network;

import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageAutoSyncTileField<F> extends MessageBase {
    private TileEntityBase tile;
    private int id;
    private CompoundTag tag;

    public MessageAutoSyncTileField() {
        super();
    }

    public MessageAutoSyncTileField(TileEntityBase.AutoSyncedField<F> field) {
        this();
        this.tile = field.getTile();
        this.id = field.getId();
        this.tag = field.serialize();
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.tile != null && this.tag != null) {
            TileEntityBase.AutoSyncedField<F> field = tile.getField(this.id);
            if(field != null) {
                field.deserialize(this.tag);
            }
        }
    }
}
