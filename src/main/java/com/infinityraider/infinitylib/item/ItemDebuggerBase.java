package com.infinityraider.infinitylib.item;

import com.infinityraider.infinitylib.reference.Names;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import com.infinityraider.infinitylib.utility.debug.DebugModeFeedback;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for a debug item, allows a single item to have multiple debug modes
 */
@SuppressWarnings("unused")
public abstract class ItemDebuggerBase extends ItemBase {
    private final List<DebugMode> DEBUG_MODES;

    public ItemDebuggerBase() {
        this(true);
    }

    public ItemDebuggerBase(boolean isVanilla) {
        super("debugger", new Item.Properties());
        this.DEBUG_MODES = new ArrayList<>();
        this.DEBUG_MODES.add(new DebugModeFeedback());
        this.DEBUG_MODES.addAll(getDebugModes());
    }

    protected abstract List<DebugMode> getDebugModes();

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isDiscrete()) {
            if (!world.isClientSide()) {
                DebugMode mode = this.changeDebugMode(stack);
                player.sendMessage(new TextComponent("Set debug mode to " + mode.debugName()), Util.NIL_UUID);
            }
        } else {
            this.getDebugMode(stack).debugActionClicked(stack, world, player, hand);
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getPlayer() != null && !context.getPlayer().isDiscrete()) {
            ItemStack stack = context.getItemInHand();
            this.getDebugMode(stack).debugActionBlockClicked(stack, context);
        }
        return InteractionResult.PASS;
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, Player player, @Nonnull LivingEntity target, @Nonnull InteractionHand hand) {
        if(!player.isDiscrete()) {
            this.getDebugMode(stack).debugActionEntityClicked(stack, player, target, hand);
        }
        return InteractionResult.PASS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, List<Component> tooltip, @Nonnull TooltipFlag flag) {
        DebugMode mode = getDebugMode(stack);
        tooltip.add(new TextComponent("Right Click to use the debugger in its current mode"));
        tooltip.add(new TextComponent("Shift + Right Click to cycle debug modes"));
        tooltip.add(new TextComponent("Current debug mode: "  + (mode == null ? "null" : mode.debugName())));
    }

    public DebugMode getDebugMode(ItemStack stack) {
        CompoundTag tag;
        if(!stack.hasTag() || stack.getTag() == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        } else {
            tag = stack.getTag();
        }
        if(!tag.contains(Names.NBT.COUNT)) {
            tag.putInt(Names.NBT.COUNT, 0);
        }
        return DEBUG_MODES.get(tag.getInt(Names.NBT.COUNT) % DEBUG_MODES.size());
    }

    public DebugMode changeDebugMode(ItemStack stack) {
        CompoundTag tag;
        if(!stack.hasTag() || stack.getTag() == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        } else {
            tag = stack.getTag();
        }
        int index;
        if(!tag.contains(Names.NBT.COUNT)) {
            index = 1;
        } else {
            index = (tag.getInt(Names.NBT.COUNT) + 1 ) % DEBUG_MODES.size();
        }
        tag.putInt(Names.NBT.COUNT, index);
        return DEBUG_MODES.get(index);
    }
}
