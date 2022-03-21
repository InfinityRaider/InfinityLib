package com.infinityraider.infinitylib.particle;

import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public interface IInfinityParticleType<D extends ParticleOptions> extends IInfinityRegistrable<ParticleType<?>> {
    D deserializeData(@Nonnull StringReader reader) throws CommandSyntaxException;

    D readData(@Nonnull FriendlyByteBuf buffer);

    @Nonnull
    ParticleFactorySupplier<D> particleFactorySupplier();

    @Override
    @SuppressWarnings("unchecked")
    default ParticleType<D> cast() {
        return (ParticleType<D>) IInfinityRegistrable.super.cast();
    }

    @FunctionalInterface
    interface ParticleFactorySupplier<T extends ParticleOptions> {
        @OnlyIn(Dist.CLIENT)
        ParticleProvider<T> supplyFactory();
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    static <D extends ParticleOptions> ParticleOptions.Deserializer<D> deserializer() {
        return (ParticleOptions.Deserializer<D>) Deserializer.INSTANCE;
    }

    final class Deserializer {
        @SuppressWarnings({"unchecked", "deprecation"})
        private static final ParticleOptions.Deserializer<?> INSTANCE = new ParticleOptions.Deserializer<ParticleOptions>() {
            @Nonnull
            @Override
            public final ParticleOptions fromCommand(@Nonnull ParticleType type, @Nonnull StringReader reader) throws CommandSyntaxException {
                return this.cast(type).deserializeData(reader);
            }

            @Nonnull
            @Override
            public final ParticleOptions fromNetwork(@Nonnull ParticleType type, @Nonnull FriendlyByteBuf buffer) {
                return this.cast(type).readData(buffer);
            }

            private IInfinityParticleType<?> cast(ParticleType<?> type) {
                return (IInfinityParticleType<?>) type;
            }
        };

        private Deserializer() {}
    }

}
