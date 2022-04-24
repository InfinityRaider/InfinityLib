package com.infinityraider.infinitylib.utility;

import com.mojang.datafixers.util.Either;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FuzzyStack {
    private final Either<Item, ITag<Item>> item;
    private final int count;
    private final CompoundTag nbt;
    private final List<String> ignoredNbt;

    public FuzzyStack(Item item) {
        this(item, null);
    }

    public FuzzyStack(ITag<Item> item) {
        this(item, null);
    }

    public FuzzyStack(Item item, int count) {
        this(item, count, null);
    }

    public FuzzyStack(ITag<Item> item, int count) {
        this(item, count, null);
    }

    public FuzzyStack(Item item, @Nullable CompoundTag nbt) {
        this(item, 1, nbt);
    }

    public FuzzyStack(ITag<Item> item, @Nullable CompoundTag nbt) {
        this(item, 1, nbt);
    }

    public FuzzyStack(Item item, int count, @Nullable CompoundTag nbt) {
        this(item, count, nbt, null);
    }

    public FuzzyStack(ITag<Item> item, int count, @Nullable CompoundTag nbt) {
        this(item, count, nbt, null);
    }

    public FuzzyStack(Item item, int count, @Nullable CompoundTag nbt, @Nullable List<String> ignoreNbt) {
        // Perform Parameter Validation
        Objects.requireNonNull(item, "The Item must not be null for FuzzyStacks!");

        // Perform Assignments
        this.item = Either.left(item);
        this.count = count;
        this.ignoredNbt = Optional.ofNullable(ignoreNbt).orElseGet(Collections::emptyList);
        this.nbt = stripNbt(nbt);
    }

    public FuzzyStack(ITag<Item> item, int count, @Nullable CompoundTag nbt, @Nullable List<String> ignoreNbt) {
        // Perform Parameter Validation
        Objects.requireNonNull(item, "The Item must not be null for FuzzyStacks!");

        // Perform Assignments
        this.item = Either.right(item);
        this.count = count;
        this.ignoredNbt = Optional.ofNullable(ignoreNbt).orElseGet(Collections::emptyList);
        this.nbt = stripNbt(nbt);
    }

    public static Optional<FuzzyStack> from(@Nullable ItemStack stack) {
        return Optional.ofNullable(stack)
                .map(s -> new FuzzyStack(s.getItem(), s.getTag()));
    }

    public Stream<Item> stream() {
        return this.item.map(Stream::of, ITag::stream);
    }

    public void foreach(Consumer<Item> consumer) {
        this.item.ifLeft(consumer);
        this.item.ifRight(tag -> tag.stream().forEach(consumer));
    }

    public ItemStack toStack() {
        final ItemStack stack = this.item.map(ItemStack::new, tag -> tag.stream().findFirst().map(ItemStack::new).orElse(ItemStack.EMPTY));
        if(!stack.isEmpty()) {
            stack.setTag(this.nbt.getAllKeys().isEmpty() ? null : this.nbt.copy());
            stack.setCount(this.count);
        }
        return stack;
    }

    public Collection<ItemStack> allStacks() {
         return this.item.map(Stream::of, ITag::stream)
                 .map(ItemStack::new)
                 .filter(stack -> !stack.isEmpty())
                 .peek(stack -> stack.setTag(this.nbt.getAllKeys().isEmpty() ? null : this.nbt.copy()))
                 .peek(stack -> stack.setCount(this.count))
                 .collect(Collectors.toList());
    }

    public CompoundTag getNbt() {
        return this.nbt.copy();
    }

    public boolean doesItemMatch(Item other) {
        return this.item.map(item -> item.equals(other), tag -> tag.contains(other));
    }

    public boolean doesItemMatch(ItemStack other) {
        return this.doesItemMatch(other.getItem());
    }

    public boolean doesItemMatch(ITag<Item> other) {
        return this.item.map(other::contains, tag -> tag.equals(other));
    }

    public boolean doesItemMatch(FuzzyStack other) {
        return other.item.map(this::doesItemMatch, this::doesItemMatch);
    }

    public boolean doesNbtMatch(@Nullable ItemStack other) {
        return (other != null) && this.getNbt().equals(stripNbt(other.getTag()));
    }

    public boolean doesNbtMatch(@Nullable FuzzyStack other) {
        return (other != null) && other.stripNbt(this.getNbt()).equals(this.stripNbt(other.getNbt()));
    }

    private CompoundTag stripNbt(@Nullable CompoundTag tag) {
        if ((tag == null) || this.ignoredNbt.contains("*")) {
            return new CompoundTag();
        } else {
            final CompoundTag stripped = tag.copy();
            this.ignoredNbt.forEach(stripped::remove);
            return stripped;
        }
    }

    public boolean matches(@Nullable Object obj) {
        if (obj instanceof ItemLike) {
            return this.matches(new FuzzyStack(((ItemLike) obj).asItem()));
        } else if (obj instanceof ItemStack) {
            ItemStack other = (ItemStack) obj;
            return this.matches(new FuzzyStack(other.getItem(), other.getTag()));
        } else if (obj instanceof FuzzyStack) {
            FuzzyStack other = (FuzzyStack) obj;
            return this.doesItemMatch(other) && this.doesNbtMatch(other);
        }
        return false;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // If objects are identical, return quick true
        if(obj == this) {
            return true;
        }
        // If object is a fuzzy stack as well, do checks
        if (obj instanceof FuzzyStack) {
            FuzzyStack other = (FuzzyStack) obj;
            // check if item definition is equal
            boolean item = this.item.map(
                    this_item -> other.item.map(other_item -> other_item.equals(this_item), tag -> false),
                    this_tag -> other.item.map(other_item -> false, other_tag -> other_tag.equals(this_tag))
            );
            if(!item) {
                return false;
            }
            // check if count is equal
            if(this.count != other.count) {
                return false;
            }
            // check if nbt is equal
            if(!this.nbt.equals(other.nbt)) {
                return false;
            }
            // check if ignored nbt is equal
            if(this.ignoredNbt.size() != other.ignoredNbt.size()) {
                return false;
            }
            for(int i = 0; i < this.ignoredNbt.size(); i++) {
                if(!this.ignoredNbt.get(i).equals(other.ignoredNbt.get(i))) {
                    return false;
                }
            }
            // all checks passed
            return true;
        }
        // Default to false
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.item);
        hash = 97 * hash + this.count;
        hash = 97 * hash + Objects.hashCode(this.nbt);
        hash = 97 * hash + Objects.hashCode(this.ignoredNbt);
        return hash;
    }

    @Override
    public String toString() {
        return this.item.toString();
    }
}
