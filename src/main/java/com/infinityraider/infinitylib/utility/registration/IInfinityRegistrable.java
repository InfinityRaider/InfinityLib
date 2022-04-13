package com.infinityraider.infinitylib.utility.registration;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.utility.IToggleable;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

public interface IInfinityRegistrable<T extends IForgeRegistryEntry<T>> extends IForgeRegistryEntry<T>, IToggleable {

    /**
     * The name used internally as to represent this block, not including the modid qualifier.
     *
     * @return the internal name of the block.
     */
    @Nonnull
    String getInternalName();

    /**
     * Method to self cast to Block
     * @return this, but typecast as Block
     */
    @SuppressWarnings("unchecked")
    default T cast() {
        try {
            return (T) this;
        } catch(Exception e) {
            InfinityLib.instance.getLogger().printStackTrace(e);
        }
        throw new IllegalArgumentException("IInfinityObject must only be implemented in objects extending its parametric type");
    }

}
