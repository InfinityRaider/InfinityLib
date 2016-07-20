package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.reference.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemBase extends Item implements IItemWithModel {
    private final String internalName;

    public ItemBase(String name) {
        super();
        this.internalName = name;
    }

    public boolean isEnabled() {
        return true;
    }

    public String getInternalName() {
        return internalName;
    }

    public abstract List<String> getOreTags();

    @Override
    @SideOnly(Side.CLIENT)
    public List<Tuple<Integer, ModelResourceLocation>> getModelDefinitions() {
        List<Tuple<Integer, ModelResourceLocation>> list = new ArrayList<>();
        list.add(new Tuple<>(0, new ModelResourceLocation(Reference.MOD_ID.toLowerCase()+ ":" + internalName, "inventory")));
        return list;
    }
}