package com.infinityraider.infinitylib.modules.keyboard;

import com.google.common.collect.Sets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class KeyEventHandler {
    private static final KeyEventHandler INSTANCE = new KeyEventHandler();

    public static KeyEventHandler getInstance() {
        return INSTANCE;
    }

    private final Map<Integer, KeyTracker> trackers;

    private KeyEventHandler() {
        this.trackers = new HashMap<>();
    }

    private KeyTracker getTracker(int key) {
        if(this.trackers.containsKey(key)) {
            return trackers.get(key);
        } else {
            KeyTracker tracker = new KeyTracker(key);
            this.trackers.put(key, tracker);
            return tracker;
        }
    }

    protected void registerListener(IKeyListener listener) {
        listener.keys().forEach(key -> {
            this.getTracker(key).addListener(listener);
        });
    }

    protected boolean isKeyPressed(int key) {
        return this.getTracker(key).isPressed();
    }

    protected boolean isKeyRepeated(int key) {
        return this.getTracker(key).isRepeated();
    }

    protected int getKeyHoldDownTime(int key) {
        return this.getTracker(key).getCount();
    }

    protected int getKeyModifier(int key) {
        return this.getTracker(key).getModifier();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        KeyTracker tracker = this.getTracker(event.getKey());
        switch (event.getAction()) {
            case GLFW.GLFW_PRESS: tracker.onPress(event.getModifiers()); break;
            case GLFW.GLFW_RELEASE: tracker.onRelease(event.getModifiers()); break;
            case GLFW.GLFW_REPEAT: tracker.onRepeat(event.getModifiers()); break;
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            this.trackers.values().forEach(KeyTracker::onTick);
        }
    }

    private static class KeyTracker {
        private final int key;
        private final Set<IKeyListener> listeners;

        private KeyStatus status;
        private int modifier;
        private int count;

        private KeyTracker(int key) {
            this.key = key;
            this.listeners = Sets.newIdentityHashSet();
            this.status = KeyStatus.UNPRESSED;
            this.modifier = -1;
            this.count = 0;
        }

        public int getKey() {
            return this.key;
        }

        public KeyStatus getStatus() {
            return this.status;
        }

        public int getModifier() {
            return this.modifier;
        }

        public int getCount() {
            return this.count;
        }

        public boolean isPressed() {
            return this.getStatus() == KeyStatus.PRESSED;
        }

        public boolean isRepeated() {
            return this.getStatus() == KeyStatus.REPEATED;
        }

        public KeyTracker addListener(IKeyListener listener) {
            if(listener.keys().contains(this.getKey())) {
                this.listeners.add(listener);
            }
            return this;
        }

        public void onPress(int modifier) {
            this.status = KeyStatus.PRESSED;
            this.modifier = modifier;
            this.count = 0;
            this.listeners.forEach(l -> l.onKeyPress(this.getKey(), this.getModifier()));
        }

        public void onRelease(int modifier) {
            this.status = KeyStatus.UNPRESSED;
            this.modifier = modifier;
            this.listeners.forEach(l -> l.onKeyReleased(this.getKey(), this.getModifier(), this.getCount()));
        }

        public void onRepeat(int modifier) {
            this.status = KeyStatus.REPEATED;
            this.modifier = modifier;
            this.listeners.forEach(l -> l.onKeyRepeated(this.getKey(), this.getModifier()));
        }

        public void onTick() {
            switch (this.getStatus()) {
                case UNPRESSED:
                    this.count = 0;
                    this.modifier = -1;
                    break;
                case PRESSED:
                    this.count++;
                    this.listeners.forEach(l -> l.whileKeyHeld(this.getKey(), this.getModifier(), this.getCount()));
                    break;
                case REPEATED:
                    this.status = KeyStatus.UNPRESSED;
                    this.count = 0;
                    this.modifier = -1;
            }
        }
    }

    private enum KeyStatus {
        UNPRESSED,
        PRESSED,
        REPEATED
    }
}
