package com.infinityraider.infinitylib.render.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;

@OnlyIn(Dist.CLIENT)
public class ModelLoaderRegistrar {
    private static final ModelLoaderRegistrar INSTANCE = new ModelLoaderRegistrar();

    public static final ModelLoaderRegistrar getInstance() {
        return INSTANCE;
    }

    public final InfModelLoader<?> infCompositeModelLoader;

    private ModelLoaderRegistrar() {
        this.infCompositeModelLoader = InfModelLoaderComposite.INSTANCE;
    }

    @SuppressWarnings("unused")
    public final void registerModelLoaders(final ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(infCompositeModelLoader.getId(), infCompositeModelLoader);
    }
}
