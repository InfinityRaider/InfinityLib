package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.utility.IToggleable;

import java.util.List;

public interface IInfinityItem extends IToggleable {
    public String getInternalName();

    List<String> getOreTags();
}
