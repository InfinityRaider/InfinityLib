package com.infinityraider.infinitylib.modules.playeranimations;

import com.infinityraider.infinitylib.modules.Module;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModulePlayerAnimations extends Module {
    private static final ModulePlayerAnimations INSTANCE = new ModulePlayerAnimations();

    public static ModulePlayerAnimations getInstance() {
        return INSTANCE;
    }

    private ModulePlayerAnimations() {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void postInitClient() {
        //ModelPlayerCustomized.replaceOldModel();
    }
}
