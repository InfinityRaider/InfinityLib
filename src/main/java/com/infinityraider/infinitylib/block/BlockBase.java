package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.blockstate.BlockStateWithPos;
import com.infinityraider.infinitylib.block.blockstate.IBlockStateWithPos;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public abstract class BlockBase extends Block implements IInfinityBlock {
    private final String internalName;

    public BlockBase(String name, Material blockMaterial) {
        super(blockMaterial);
        this.internalName = name;
        this.setDefaultState();
    }

    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getInternalName() {
        return this.internalName;
    }

    @Override
    protected final BlockStateContainer createBlockState() {
        InfinityProperty[] propertyArray = this.getPropertyArray();
        IProperty[] properties = new IProperty[propertyArray.length];
        for(int i = 0; i < properties.length; i++) {
            properties[i] = propertyArray[i].getProperty();
        }
        IUnlistedProperty[] uprops = getUnlistedPropertyArray();
        if (uprops.length < 1) {
            return new BlockStateContainer(this, properties);
        } else {
            return new ExtendedBlockState(this, properties, uprops);
        }
    }

    private void setDefaultState() {
        IBlockState state = this.blockState.getBaseState();
        for(InfinityProperty property : this.getPropertyArray()) {
            state.withProperty(property.getProperty(), property.getDefault());
        }
    }

    /**
     * @return a property array containing all properties for this block's state
     */
    protected abstract InfinityProperty[] getPropertyArray();
    
    /**
     * @return a property array containing all properties for this block's state
     */
    protected IUnlistedProperty[] getUnlistedPropertyArray() {
        return new IUnlistedProperty[0];
    }


    @Override
    @SuppressWarnings("unchecked")
    public final IBlockStateWithPos<? extends IBlockState> getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new BlockStateWithPos<>(extendedState(state, world, pos), pos);
    }

    protected IBlockState extendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
