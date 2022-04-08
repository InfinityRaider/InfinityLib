package com.infinityraider.infinitylib.utility;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public final class TooltipRegion<T> {
    private final Function<T, List<Component>> tooltip;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final BooleanSupplier isActive;

    public TooltipRegion(Component tooltip, int x1, int y1, int x2, int y2) {
        this(ImmutableList.of(tooltip), x1, y1, x2, y2);
    }

    public TooltipRegion(Function<T, List<Component>> tooltip, int x1, int y1, int x2, int y2) {
        this(tooltip, x1, y1, x2, y2, () -> true);
    }

    public TooltipRegion(Component tooltip, int x1, int y1, int x2, int y2, BooleanSupplier isActive) {
        this(ImmutableList.of(tooltip), x1, y1, x2, y2, isActive);
    }

    public TooltipRegion(List<Component> tooltip, int x1, int y1, int x2, int y2) {
        this(tooltip, x1, y1, x2, y2, () -> true);
    }

    public TooltipRegion(List<Component> tooltip, int x1, int y1, int x2, int y2, BooleanSupplier isActive) {
        this((state) -> tooltip, x1, y1, x2, y2, isActive);
    }

    public TooltipRegion(Function<T, List<Component>> tooltip, int x1, int y1, int x2, int y2, BooleanSupplier isActive) {
        this.tooltip = tooltip;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.isActive = isActive;
    }

    public boolean isActive(double mX, double mY) {
        return this.isActive.getAsBoolean() && mX >= this.x1 && mX <= this.x2 && mY >= this.y1 && mY <= this.y2;
    }

    public List<Component> getTooltips(T target) {
        return this.tooltip.apply(target);
    }
}
