package com.infinityraider.infinitylib.particle;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.IParticleData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ParticleHelper {
    private static final ParticleHelper INSTANCE = new ParticleHelper();

    public static ParticleHelper getInstance() {
        return INSTANCE;
    }

    private final Set<IInfinityParticleType<?>> types;

    private ParticleHelper() {
        this.types = Sets.newConcurrentHashSet();
    }

    public <T extends IParticleData> void registerType(IInfinityParticleType<T> type) {
        this.types.add(type);
    }

    @SuppressWarnings({"unchecked", "unused"})
    public void onFactoryRegistration(ParticleFactoryRegisterEvent event) {
        this.types.forEach(this::registerFactory);
    }

    protected <T extends IParticleData> void registerFactory(IInfinityParticleType<T> type) {
        Minecraft.getInstance().particles.registerFactory(type.cast(), type.particleFactorySupplier().supplyFactory());
    }
}
