package com.infinityraider.infinitylib;

import com.infinityraider.infinitylib.utility.LogHelper;
import com.infinityraider.infinitylib.utility.ModHelper;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class InfinityModRegistry {
    private static final InfinityModRegistry INSTANCE = new InfinityModRegistry();

    public static InfinityModRegistry getInstance() {
        return INSTANCE;
    }

    private final List<IInfinityMod> mods;

    private InfinityModRegistry() {
        this.mods = new ArrayList<>();
    }

    public void preInit(FMLPreInitializationEvent event) {
        this.mods.addAll((getInstances(event.getAsmData(), InfinityMod.class, IInfinityMod.class)));
        for(IInfinityMod mod : mods) {
            ModHelper.getInstance().onPreInit(event, mod);
        }
    }

    public void init(FMLInitializationEvent event) {
        for(IInfinityMod mod : mods) {
            ModHelper.getInstance().onInit(event, mod);
        }

    }

    public void postInit(FMLPostInitializationEvent event) {
        for(IInfinityMod mod : mods) {
            ModHelper.getInstance().onPostInit(event, mod);
        }
    }

    @SideOnly(Side.CLIENT)
    public void initRenderers() {
        for(IInfinityMod mod : mods) {
            ModHelper.getInstance().initRenderers(mod);
        }
    }

    /**
     * Loads classes with a specific annotation from an asm data table.
     *
     * Borrowed from JEI's source code, which is licensed under the MIT license.
     *
     * @param <T> The type of class to load.
     * @param asm The asm data table to load classes from.
     * @param annotation The annotation marking classes of interest.
     * @param type The class type to load, as to get around Type erasure.
     * @return A list of the loaded classes, instantiated.
     */
    private <T> List<T> getInstances(ASMDataTable asm, Class annotation, Class<T> type) {
        List<T> instances = new ArrayList<>();
        for (ASMDataTable.ASMData asmData : asm.getAll(annotation.getCanonicalName())) {
            try {
                T instance = Class.forName(asmData.getClassName()).asSubclass(type).newInstance();
                instances.add(instance);
            } catch (ClassNotFoundException | NoClassDefFoundError | IllegalAccessException | InstantiationException e) {
                LogHelper.debug(
                        "%nFailed to load mod%n\tOf class: " +
                                asmData.getClassName() +
                                "%n\tFor annotation: " +
                                annotation.getCanonicalName() +
                                "%n\tAs Instanceof: " +
                                type.getCanonicalName()
                );
            }
        }
        return instances;
    }
}
