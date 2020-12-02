package com.infinityraider.infinitylib.utility;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public class TranslationHelper {
    public static String translateToLocal(String text) {
        return I18n.format(text);
    }
}
