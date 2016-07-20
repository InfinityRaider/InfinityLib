package com.infinityraider.infinitylib.utility.debug;

import java.util.List;

/**
 * Implement this in TileEntity classes to be able to add debug info to a list when the TileEntity is right clicked by SettlerCraft's debug item
 */
public interface IDebuggable {
    void addDebugInfo(List<String> list);
}