package com.infinityraider.infinitylib.render.model;

import com.google.common.collect.Sets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ModelLoaderRegistrar {
    private static final ModelLoaderRegistrar INSTANCE = new ModelLoaderRegistrar();

    public static final ModelLoaderRegistrar getInstance() {
        return INSTANCE;
    }

    private final Set<InfModelLoader<?>> loaders;

    private ModelLoaderRegistrar() {
        this.loaders = Sets.newConcurrentHashSet();
    }

    public <T extends IModelGeometry<T>> void registerModelLoader(InfModelLoader<T> loader) {
        this.loaders.add(loader);
    }

    @SuppressWarnings("unused")
    public final void registerModelLoaders(final ModelRegistryEvent event) {
        this.loaders.forEach((loader) -> ModelLoaderRegistry.registerLoader(loader.getId(), loader));
    }
}
