package com.infinityraider.infinitylib.modules.keyboard;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
public interface IKeyListener {
    Set<Integer> keys();

    void onKeyPress(int key, int modifier);

    void whileKeyHeld(int key, int modifier, int duration);

    void onKeyReleased(int key, int modifier, int duration);

    void onKeyRepeated(int key, int modifier);
}
