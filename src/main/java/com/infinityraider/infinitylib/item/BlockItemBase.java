package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.block.IInfinityBlock;
import net.minecraft.item.BlockItem;

import java.util.Collections;
import java.util.List;

public class BlockItemBase extends BlockItem implements IInfinityItem {

    private final String internalName;

    public BlockItemBase(IInfinityBlock block, Properties properties) {
        super(block.cast(), properties);
        this.internalName = block.getInternalName();
    }

    public boolean isEnabled() {
        return true;
    }

    public String getInternalName() {
        return internalName;
    }

    public List<String> getIgnoredNBT() {
        return Collections.emptyList();
    }

}
