package com.infinityraider.infinitylib.particle;

import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public interface IInfinityParticleType<D extends IParticleData> extends IInfinityRegistrable<ParticleType<?>> {
    D deserializeData(@Nonnull StringReader reader) throws CommandSyntaxException;

    D readData(@Nonnull PacketBuffer buffer);

    @Nonnull
    ParticleFactorySupplier<D> particleFactorySupplier();

    @Override
    @SuppressWarnings("unchecked")
    default ParticleType<D> cast() {
        return (ParticleType<D>) IInfinityRegistrable.super.cast();
    }

    @FunctionalInterface
    interface ParticleFactorySupplier<T extends IParticleData> {
        @OnlyIn(Dist.CLIENT)
        IParticleFactory<T> supplyFactory();
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    static <D extends IParticleData> IParticleData.IDeserializer<D> deserializer() {
        return (IParticleData.IDeserializer<D>) Deserializer.INSTANCE;
    }

    final class Deserializer {
        @SuppressWarnings({"unchecked", "deprecation"})
        private static final IParticleData.IDeserializer<?> INSTANCE = new IParticleData.IDeserializer<IParticleData>() {
            @Nonnull
            @Override
            public final IParticleData deserialize(@Nonnull ParticleType type, @Nonnull StringReader reader) throws CommandSyntaxException {
                return this.cast(type).deserializeData(reader);
            }

            @Nonnull
            @Override
            public final IParticleData read(@Nonnull ParticleType type, @Nonnull PacketBuffer buffer) {
                return this.cast(type).readData(buffer);
            }

            private IInfinityParticleType<?> cast(ParticleType<?> type) {
                return (IInfinityParticleType<?>) type;
            }
        };

        private Deserializer() {}
    }

}
