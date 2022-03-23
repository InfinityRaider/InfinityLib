package com.infinityraider.infinitylib.container;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InfinityContainerMenuType<T extends ContainerMenuBase> extends MenuType<T> implements IInfinityContainerMenuType {
    private final String name;
    private final IGuiFactory<T> guiFactory;

    private InfinityContainerMenuType(String name, IContainerFactory<T> factory, IGuiFactory<T> guiFactory) {
        super(factory);
        this.name = name;
        this.guiFactory = guiFactory;
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

    public static <T extends ContainerMenuBase> Builder<T> builder(String name, IContainerFactory<T> factory) {
        return new Builder<>(name, factory);
    }

    @Override
    @Nullable
    public IGuiFactory<T> getGuiFactory() {
        return this.guiFactory;
    }

    public static final class Builder<T extends ContainerMenuBase> {
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

        public InfinityContainerMenuType<T> build() {
            return new InfinityContainerMenuType<>(this.name, this.factory, this.guiFactory);
        }
    }

}
