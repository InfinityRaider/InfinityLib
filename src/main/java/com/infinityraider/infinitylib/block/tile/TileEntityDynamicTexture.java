package com.infinityraider.infinitylib.block.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;

public abstract class TileEntityDynamicTexture extends TileEntityBase {
    public static final ModelProperty<ItemStack> PROPERTY_MATERIAL = new ModelProperty<>();

    private final AutoSyncedField<ItemStack> material;
    private final ModelDataMap data;

    public TileEntityDynamicTexture(TileEntityType<?> type) {
        super(type);
        this.data = new ModelDataMap.Builder().withInitial(PROPERTY_MATERIAL, ItemStack.EMPTY).build();
        this.material = this.getAutoSyncedFieldBuilder(ItemStack.EMPTY)
                .withRenderUpdate()
                .withCallBack(material -> this.getModelData().setData(PROPERTY_MATERIAL, material))
                .build();
    }

    public ItemStack getMaterial() {
        return this.material.get();
    }

    public void setMaterial(ItemStack material) {
        this.material.set(material);
    }

    public boolean isSameMaterial(TileEntity other) {
        if(!(other instanceof TileEntityDynamicTexture)) {
            return false;
        }
        return this.isSameMaterial(((TileEntityDynamicTexture) other).getMaterial());
    }

    public boolean isSameMaterial(ItemStack material) {
        return ItemStack.areItemsEqual(this.getMaterial(), material);
    }

    @Nonnull
    @Override
    public final ModelDataMap getModelData() {
        return this.data;
    }
}
