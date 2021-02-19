package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.reference.Names;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockItemDynamicTexture extends BlockItemBase {
    public BlockItemDynamicTexture(BlockDynamicTexture<?> block, Properties properties) {
        super(block, properties);
    }

    public void setMaterial(ItemStack stack, ItemStack material) {
        CompoundNBT tag = stack.getTag();
        if(tag == null) {
            tag = new CompoundNBT();
            stack.setTag(tag);
        }
        tag.put(Names.NBT.MATERIAL, material.write(new CompoundNBT()));
    }

    public ItemStack getMaterial(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if(tag == null || !tag.contains(Names.NBT.MATERIAL)) {
            return ItemStack.EMPTY;
        }
        return ItemStack.read(tag.getCompound(Names.NBT.MATERIAL));
    }

    private static final TranslationTextComponent TOOLTIP = new TranslationTextComponent(InfinityLib.instance.getModId() + ".tooltip.material");
    private static final StringTextComponent COLON = new StringTextComponent(": ");
    private static final TranslationTextComponent UNKNOWN = new TranslationTextComponent(InfinityLib.instance.getModId() + ".tooltip.unknown");

    @Override
    @OnlyIn(Dist.CLIENT)
    public final void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag advanced) {
        ItemStack material = this.getMaterial(stack);
        tooltip.clear();
        if(stack.isEmpty()) {
            tooltip.add(TOOLTIP.append(COLON).append(UNKNOWN));
        } else {
            tooltip.add(TOOLTIP.append(COLON).append(material.getDisplayName()));
        }
        this.addMoreInformation(stack, world, tooltip, advanced);
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unused")
    protected void addMoreInformation (@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag advanced) {
        // NOOP (for sub classes)
    }
}
