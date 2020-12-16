package com.infinityraider.infinitylib.render.model;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public interface InfModelLoader<T extends IModelGeometry<T>> extends IModelLoader<T> {
    ResourceLocation getId();
}
