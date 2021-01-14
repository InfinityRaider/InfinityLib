package com.infinityraider.infinitylib.network;

import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageAutoSyncTileField<F> extends MessageBase {
    private TileEntityBase tile;
    private int id;
    private CompoundNBT tag;

    public MessageAutoSyncTileField() {
        super();
    }

    public MessageAutoSyncTileField(TileEntityBase.AutoSyncedField<F> field) {
        this();
        this.tile = field.getTile();
        this.id = field.getId();
        this.tag = field.serialize(new CompoundNBT());
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
