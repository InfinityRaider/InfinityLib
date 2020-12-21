package com.infinityraider.infinitylib.container;

import com.infinityraider.infinitylib.utility.IInfinityRegistrable;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface IInfinityContainerType extends IInfinityRegistrable<ContainerType<?>> {

    @Nullable
    IGuiFactory<?> getGuiFactory();

    interface IGuiFactory<T extends Container> {
        @OnlyIn(Dist.CLIENT)
        <U extends Screen & IHasContainer<T>> ScreenManager.IScreenFactory<T, U> getGuiScreenProvider();
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    static <T extends Container, U extends Screen & IHasContainer<T>> ScreenManager.IScreenFactory<T,U> castGuiFactory(IGuiFactory factory) {
        return factory.getGuiScreenProvider();
    }
}
