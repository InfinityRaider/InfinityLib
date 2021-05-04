package com.infinityraider.infinitylib.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

import javax.annotation.Nonnull;

public class EnchantmentBase extends Enchantment implements IInfinityEnchantment {
    private final String name;
    private String displayName;

    protected EnchantmentBase(String name, Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
        super(rarityIn, typeIn, slots);
        this.name = name;
    }

    public final void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    protected final String getDefaultTranslationKey() {
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
