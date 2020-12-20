package com.infinityraider.infinitylib.container;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.network.IContainerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InfinityContainerType<T extends ContainerBase> extends ContainerType<T> implements IInfinityContainerType {
    private final String name;
    private final IGuiFactory<T> guiFactory;

    private InfinityContainerType(String name, IContainerFactory<T> factory, IGuiFactory<T> guiFactory) {
        super(factory);
        this.name = name;
        this.guiFactory = getGuiFactory();
    }

    @Nonnull
    @Override
    public String getInternalName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static <T extends ContainerBase> Builder<T> builder(String name, IContainerFactory<T> factory) {
        return new Builder<>(name, factory);
    }

    @Override
    @Nullable
    public IGuiFactory<T> getGuiFactory() {
        return this.guiFactory;
    }

    public static final class Builder<T extends ContainerBase> {
        private final String name;
        private final IContainerFactory<T> factory;
        private IGuiFactory<T> guiFactory;

        private Builder(String name, IContainerFactory<T> factory) {
            this.name = name;
            this.factory = factory;
        }

        public Builder<T> setGuiFactory(IGuiFactory<T> factory) {
            this.guiFactory = factory;
            return this;
        }

        public InfinityContainerType<T> build() {
            return new InfinityContainerType<>(this.name, this.factory, this.guiFactory);
        }
    }

}
