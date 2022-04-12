package com.infinityraider.infinitylib.container;

import com.infinityraider.infinitylib.utility.registration.IInfinityRegistrable;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface IInfinityContainerMenuType extends IInfinityRegistrable<MenuType<?>> {

    @Nullable
    IGuiFactory<?> getGuiFactory();

    interface IGuiFactory<T extends AbstractContainerMenu> {
        @OnlyIn(Dist.CLIENT)
        <U extends Screen & MenuAccess<T>> MenuScreens.ScreenConstructor<T, U> getGuiScreenProvider();
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    static <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> MenuScreens.ScreenConstructor<T,U> castGuiFactory(IGuiFactory factory) {
        return factory.getGuiScreenProvider();
    }
}
