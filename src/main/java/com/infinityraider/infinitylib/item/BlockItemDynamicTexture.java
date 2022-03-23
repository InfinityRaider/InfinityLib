package com.infinityraider.infinitylib.item;

import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class BlockItemDynamicTexture extends BlockItemBase {
    private static final Set<BlockItemDynamicTexture> ITEMS = Sets.newConcurrentHashSet();

    public static Set<BlockItemDynamicTexture> getAll() {
        return ITEMS;
    }

    public BlockItemDynamicTexture(BlockDynamicTexture<?> block, Properties properties) {
        super(block, properties);
        ITEMS.add(this);
    }

    public final void setMaterial(ItemStack stack, ItemStack material) {
        CompoundTag tag = stack.getTag();
        if(tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        this.setMaterial(tag, material);
    }

    protected final void setMaterial(CompoundTag tag, ItemStack material) {
        tag.put(Names.NBT.MATERIAL, material.save(new CompoundTag()));
    }

    public final ItemStack getMaterial(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        if(!tag.contains(Names.NBT.MATERIAL)) {
            this.setMaterial(stack, this.getDefaultMaterial());
        }
        return ItemStack.of(tag.getCompound(Names.NBT.MATERIAL));
    }

    public abstract ItemStack getDefaultMaterial();

    private static final Component TOOLTIP = new TranslatableComponent(InfinityLib.instance.getModId() + ".tooltip.material");
    private static final Component COLON = new TextComponent(": ");
    private static final Component UNKNOWN = new TranslatableComponent(InfinityLib.instance.getModId() + ".tooltip.unknown");

    @Override
    @OnlyIn(Dist.CLIENT)
    public final void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag advanced) {
        ItemStack material = this.getMaterial(stack);
        MutableComponent tooltip = new TextComponent("").append(TOOLTIP).append(COLON);
        if(material.isEmpty()) {
            tooltips.add(tooltip.append(UNKNOWN));
        } else {
            tooltips.add(tooltip.append(material.getDisplayName()));
        }
        this.addInformation(stack, world, tooltips::add, advanced);
    }

    @SuppressWarnings("unused")
    @OnlyIn(Dist.CLIENT)
    protected void addInformation (@Nonnull ItemStack stack, @Nullable Level world, @Nonnull Consumer<Component> tooltip, @Nonnull TooltipFlag advanced) {
        // NOOP (for sub classes)
    }
}
