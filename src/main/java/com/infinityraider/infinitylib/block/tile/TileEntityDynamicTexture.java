package com.infinityraider.infinitylib.block.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;

public abstract class TileEntityDynamicTexture extends TileEntityBase {
    public static final ModelProperty<ItemStack> PROPERTY_MATERIAL = new ModelProperty<>();

    private final AutoSyncedField<ItemStack> material;

    public TileEntityDynamicTexture(TileEntityType<?> type) {
        super(type);
        this.material = this.getAutoSyncedFieldBuilder(ItemStack.EMPTY)
                .withRenderUpdate()
                .build();
    }

    public ItemStack getMaterial() {
        return this.material.get();
    }

    public void setMaterial(ItemStack material) {
        this.material.set(material);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder();
        this.populateModelData(builder);
        return builder.withInitial(PROPERTY_MATERIAL, this.getMaterial()).build();
    }

    protected abstract void populateModelData(ModelDataMap.Builder modelDataBuilder);
}
