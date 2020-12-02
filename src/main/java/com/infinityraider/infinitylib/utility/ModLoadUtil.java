package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.InfinityMod;

import java.lang.reflect.Field;

public class ModLoadUtil {
    public static void populateStaticModInstanceField(InfinityMod mod) {
        for (Field field : mod.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(InfinityMod.class)) {
                try {
                    field.set(null, mod);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
