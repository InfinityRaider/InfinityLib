package com.infinityraider.infinitylib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation should be added to a mod's main class to have the registering of Items, Blocks, Renderers, etc. handled by InfinityLib
 * When using this annotation, the class must also implement IInfinityMod
 */
@Target(ElementType.TYPE)
public @interface InfinityMod {
}
