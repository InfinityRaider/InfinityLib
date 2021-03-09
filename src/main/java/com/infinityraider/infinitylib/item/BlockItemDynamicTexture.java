package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class BlockItemDynamicTexture extends BlockItemBase {
    public BlockItemDynamicTexture(BlockDynamicTexture<?> block, Properties properties) {
        super(block, properties);
    }

    public final void setMaterial(ItemStack stack, ItemStack material) {
        CompoundNBT tag = stack.getTag();
        if(tag == null) {
            tag = new CompoundNBT();
            stack.setTag(tag);
        }
        tag.put(Names.NBT.MATERIAL, material.write(new CompoundNBT()));
    }

    public final ItemStack getMaterial(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if(tag == null || !tag.contains(Names.NBT.MATERIAL)) {
            return ItemStack.EMPTY;
        }
        return ItemStack.read(tag.getCompound(Names.NBT.MATERIAL));
    }

    private static final ITextComponent TOOLTIP = new TranslationTextComponent(InfinityLib.instance.getModId() + ".tooltip.material");
    private static final ITextComponent COLON = new StringTextComponent(": ");
    private static final ITextComponent UNKNOWN = new TranslationTextComponent(InfinityLib.instance.getModId() + ".tooltip.unknown");

    @Override
    @OnlyIn(Dist.CLIENT)
    public final void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag advanced) {
        ItemStack material = this.getMaterial(stack);
        IFormattableTextComponent tooltip = new StringTextComponent("").append(TOOLTIP).append(COLON);
        if(material.isEmpty()) {
            tooltips.add(tooltip.append(UNKNOWN));
        } else {
            tooltips.add(tooltip.append(material.getDisplayName()));
        }
        this.addInformation(stack, world, tooltips::add, advanced);
    }

    @SuppressWarnings("unused")
    @OnlyIn(Dist.CLIENT)
    protected void addInformation (@Nonnull ItemStack stack, @Nullable World world, @Nonnull Consumer<ITextComponent> tooltip, @Nonnull ITooltipFlag advanced) {
        // NOOP (for sub classes)
    }
}
