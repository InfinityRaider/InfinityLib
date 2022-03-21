package com.infinityraider.infinitylib.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import javax.annotation.Nonnull;

public class EnchantmentBase extends Enchantment implements IInfinityEnchantment {
    private final String name;
    private String displayName;

    protected EnchantmentBase(String name, Rarity rarityIn, EnchantmentCategory type, EquipmentSlot[] slots) {
        super(rarityIn, type, slots);
        this.name = name;
    }

    public final void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    protected final String getOrCreateDescriptionId() {
        return this.displayName;
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
}
