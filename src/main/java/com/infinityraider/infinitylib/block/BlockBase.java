package com.infinityraider.infinitylib.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;

import java.util.List;

public abstract class BlockBase extends Block {
    private final String internalName;

    public BlockBase(String name, Material blockMaterial) {
        super(blockMaterial);
        this.internalName = name;
    }

    public boolean isEnabled() {
        return true;
    }

    public String getInternalName() {
        return this.internalName;
    }

    public abstract List<String> getOreTags();

    @Override
    protected final BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getPropertyArray());
    }

    /**
     * @return a property array containing all properties for this block's state
     */
    protected abstract IProperty[] getPropertyArray();

    /**
     * Retrieves the block's ItemBlock class, as a generic class bounded by the
     * ItemBlock class.
     *
     * @return the block's class, may be null if no specific ItemBlock class is
     * desired.
     */
    public abstract Class<? extends ItemBlock> getItemBlockClass();

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
