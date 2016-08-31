package com.infinityraider.infinitylib.modules.dualwield;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

public class ModuleDualWield extends Module {
    private static final ModuleDualWield INSTANCE = new ModuleDualWield();

    public static ModuleDualWield getInstance() {
        return INSTANCE;
    }

    private ModuleDualWield() {}

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageAttackDualWielded.class);
        wrapper.registerMessage(MessageMouseButtonPressed.class);
        wrapper.registerMessage(MessageSwingArm.class);
    }

    @Override
    public List<Object> getCommonEventHandlers() {
        return Collections.emptyList();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<Object> getClientEventHandlers() {
        return ImmutableList.of(
                MouseClickHandler.getInstance(),
                ArmSwingHandler.getInstance());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void postInitClient() {
        ModelPlayerCustomized.replaceOldModel();
    }
}
