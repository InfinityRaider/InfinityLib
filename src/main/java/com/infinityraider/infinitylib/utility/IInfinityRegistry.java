package com.infinityraider.infinitylib.utility;

import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IInfinityRegistry<T extends IForgeRegistryEntry<T>> {


    interface Entry<T extends IForgeRegistryEntry<T>> extends Supplier<IInfinityRegistrable<T>>, Consumer<T>, IToggleable {

    }
}
