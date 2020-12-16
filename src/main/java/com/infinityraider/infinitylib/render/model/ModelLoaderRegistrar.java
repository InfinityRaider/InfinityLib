package com.infinityraider.infinitylib.render.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;

@OnlyIn(Dist.CLIENT)
public class InfModelLoaderRegistrar {
    private static final InfModelLoaderRegistrar INSTANCE = new InfModelLoaderRegistrar();

    public static final InfModelLoaderRegistrar getInstance() {
        return INSTANCE;
    }

    public final InfModelLoader<?> infCompositeModelLoader;

    private InfModelLoaderRegistrar() {
        this.infCompositeModelLoader = InfCompositeModelLoader.INSTANCE;
    }

    @SuppressWarnings("unused")
    public final void registerModelLoaders(final ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(infCompositeModelLoader.getId(), infCompositeModelLoader);
    }
}
