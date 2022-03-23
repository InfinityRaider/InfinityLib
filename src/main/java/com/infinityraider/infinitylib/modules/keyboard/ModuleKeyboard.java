package com.infinityraider.infinitylib.modules.keyboard;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.modules.Module;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.OptionalInt;

public class ModuleKeyboard extends Module {
    private static final ModuleKeyboard INSTANCE = new ModuleKeyboard();

    public static ModuleKeyboard getInstance() {
        return INSTANCE;
    }

    private ModuleKeyboard() {}

    @OnlyIn(Dist.CLIENT)
    public void registerKeyListener(IKeyListener listener) {
        KeyEventHandler.getInstance().registerListener(listener);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isKeyPressed(KeyMapping key) {
        return this.isKeyPressed(key.getKey().getNumericKeyValue());
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isKeyPressed(OptionalInt key) {
        return key.isPresent() && this.isKeyPressed(key.getAsInt());
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isKeyPressed(int key) {
        return KeyEventHandler.getInstance().isKeyPressed(key);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isKeyRepeated(int key) {
        return KeyEventHandler.getInstance().isKeyRepeated(key);
    }

    @OnlyIn(Dist.CLIENT)
    public int getKeyHoldDownTime(int key) {
        return KeyEventHandler.getInstance().getKeyHoldDownTime(key);
    }

    @OnlyIn(Dist.CLIENT)
    public int getKeyModifier(int key) {
        return KeyEventHandler.getInstance().getKeyModifier(key);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Object> getClientEventHandlers() {
        return ImmutableList.of(KeyEventHandler.getInstance());
    }
}
