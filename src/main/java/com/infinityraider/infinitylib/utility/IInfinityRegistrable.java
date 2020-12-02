package com.infinityraider.infinitylib.utility;

import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

public interface IInfinityRegistrable<T extends IForgeRegistryEntry<T>> extends IToggleable {

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
    @SuppressWarnings("Unchecked")
    default T cast() {
        try {
            return (T) this;
        } catch(Exception e) {}
        throw new ClassFormatException("IInfinityObject must only be implemented in objects extending its parametric type");
    }

}
