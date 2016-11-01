package com.infinityraider.infinitylib.utility.debug;

import java.util.List;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Implement this in TileEntity classes to be able to add debug info to a list
 * when the TileEntity is right clicked by a debug item.
 *
 * For uniformity of debug information, it may be assumed that the server debug
 * information will always come before the client debug information.
 */
public interface IDebuggable {

    void addServerDebugInfo(List<String> lines);

    @SideOnly(Side.CLIENT)
    default void addClientDebugInfo(List<String> lines) {
        // NOP
    }
}
