package com.infinityraider.infinitylib.utility;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.infinityraider.infinitylib.InfinityLib;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;

public final class TagUtil {
    /**
     * Determines if the given string represents a valid tag.
     *
     * @param element the string representation of the oredict entry, should start with ItemStackUtil.PREFIX_TAG.
     * @return {@literal true} if and only if the given string represents a valid oredict entry, {@literal false} otherwise.
     */
    public static boolean isValidTag(@Nullable ITagManager<?> registry, @Nullable String element) {
        // If null or empty return nothing.
        if (registry == null || element == null || element.isEmpty()) {
            return false;
        }

        // Split the element.
        final String[] parts = element.split(":");

        // If only 1 part, then assume is suffix.
        if (parts.length == 1) {
            return isValidTag(registry, "minecraft", parts[0]);
        } else if (parts.length == 2) {
            return isValidTag(registry, parts[0], parts[1]);
        } else if (parts.length > 2) {
            InfinityLib.instance.getLogger().warn("Invalid stack identifier detected!\n\tGiven: \"{0}\"\n\tAssuming: \"{1}:{2}\"", element, parts[0], parts[1]);
            return isValidTag(registry, parts[0], parts[1]);
        } else {
            throw new AssertionError("String.split() method worked incorrectly. This should be an impossible error.");
        }
    }

    /**
     * Determines if the given string represents a valid oredict resource entry.
     *
     * @param prefix
     * @param suffix
     * @return {@literal true} if and only if the given string represents a valid oredict entry, {@literal false} otherwise.
     */
    public static <T extends IForgeRegistryEntry<T>> boolean isValidTag(@Nonnull ITagManager<T> registry, @Nonnull String prefix, @Nonnull String suffix) {
        // Validate
        Preconditions.checkNotNull(prefix, "A stack identifier must have a non-null prefix!");
        Preconditions.checkNotNull(suffix, "A stack identifier must have a non-null suffix!");

        // Check that the tag registry contains the given object.
        if (registry.getTag(registry.createTagKey(new ResourceLocation(prefix, suffix))).isEmpty()) {
            InfinityLib.instance.getLogger().error("Unable to resolve Item Tag Entry: \"{0}:{1}\".", prefix, suffix);
            return false;
        } else {
            return true;
        }
    }

    private static Optional<ItemStack> createStackFromObject(Object object, int amount) {
        if(object instanceof ItemLike) {
            return Optional.of(new ItemStack((ItemLike) object, amount));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Fetches a Collection of all ItemStacks respecting the given attributes, if possible.
     *
     * @param element the registry name of the Item.
     * @param amount the stack size for the ItemStacks
     * @param useTags to check if the element corresponds to a registry name in the tag registry instead
     * @param nbt a json string representation of the tags to be associated with the item.
     * @param ignoredNbt a List of NBT tags to ignore
     * @return a Collection containing all matching ItemStacks, or empty
     */
    @Nonnull
    public static Collection<ItemStack> fetchStacks(@Nullable String element, int amount, boolean useTags, String nbt, List<String> ignoredNbt) {
        return parseStack(element, amount, useTags, nbt, ignoredNbt)
                .map(FuzzyStack::allStacks)
                .orElse(Collections.emptyList());
    }

    /**
     * Fetches a Collection of all FuzzyStacks respecting the given attributes, if possible.
     *
     * @param element the registry name of the Item.
     * @param useTags to check if the element corresponds to a registry name in the tag registry instead
     * @param nbt a json string representation of the nbt to be associated with the item.
     * @param ignoredNbt a List of NBT tags to ignore
     * @return an Optional containing a FuzzyStack, or empty
     */
    @Nonnull
    public static Optional<FuzzyStack> parseStack(@Nullable String element, int amount, boolean useTags, String nbt, List<String> ignoredNbt) {
        // If null or empty return nothing.
        if (element == null || element.isEmpty()) {
            return Optional.empty();
        }

        // Split the element.
        final String[] parts = element.split(":");

        // If only 1 part, then assume is suffix.
        if (parts.length == 1) {
            return parseStack("minecraft", parts[0], amount, useTags, nbt, ignoredNbt);
        } else if (parts.length == 2) {
            return parseStack(parts[0], parts[1], amount, useTags, nbt, ignoredNbt);
        } else if (parts.length > 2) {
            InfinityLib.instance.getLogger().warn("Invalid stack identifier detected!\n\tGiven: \"{0}\"\n\tAssuming: \"{1}:{2}\"", element, parts[0], parts[1]);
            return parseStack(parts[0], parts[1], amount, useTags, nbt, ignoredNbt);
        } else {
            throw new AssertionError("String.split() method worked incorrectly. This should be an impossible error.");
        }
        
    }

    /**
     * Creates an ItemStack with the given attributes, if possible.
     *
     * @param prefix the registry domain of the item.
     * @param suffix the registry path of the item.
     * @param amount the stack size for the ItemStacks
     * @param useTags to check if the element corresponds to a registry name in the tag registry instead
     * @param nbt a json string representation of the nbt to be associated with the item.
     * @param ignoredNbt a List of NBT tags to ignore
     * @return an Optional containing a FuzzyStack, or empty
     */
    @Nonnull
    public static Optional<FuzzyStack> parseStack(@Nonnull String prefix, @Nonnull String suffix, int amount, boolean useTags, String nbt, List<String> ignoredNbt) {
        // Validate
        Preconditions.checkNotNull(prefix, "A stack identifier must have a non-null prefix!");
        Preconditions.checkNotNull(suffix, "A stack identifier must have a non-null suffix!");

        // Test if the prefix is the special oredict prefix.
        if (useTags) {
            return parseFromTag(prefix, suffix, amount, nbt, ignoredNbt);
        } else {
            return parseFromItem(prefix, suffix, amount, nbt, ignoredNbt);
        }
    }

    @Nonnull
    private static Optional<FuzzyStack> parseFromItem(@Nonnull String prefix, @Nonnull String suffix, int amount, String nbt, List<String> ignoredNbt) {
        // Step 0. Fetch the item.
        final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(prefix, suffix));

        // Step 1. Check that item is not null.
        if (item == null) {
            InfinityLib.instance.getLogger().error("Unable to resolve item: {0}:{1}.", prefix, suffix);
            return Optional.empty();
        }

        // Step 2. Create the return.
        return Optional.of(new FuzzyStack(item, amount, parseNbt(nbt), ignoredNbt));
    }

    @Nonnull
    private static Optional<FuzzyStack> parseFromTag(@Nonnull String prefix, @Nonnull String suffix, int amount, String nbt, List<String> ignoredNbt) {
        ITagManager<Item> registry = ForgeRegistries.ITEMS.tags();
        if(registry == null) {
            return Optional.empty();
        }
        ITag<Item> tag = registry.getTag(registry.createTagKey(new ResourceLocation(prefix, suffix)));
        return Optional.of(new FuzzyStack(tag, amount, parseNbt(nbt), ignoredNbt));
    }

    @Nullable
    public static CompoundTag parseNbt(String nbt) {
        // Abort if tags are null.
        if (Strings.isNullOrEmpty(nbt)) {
            return null;
        }
        // Parse the nbt.
        try {
            return TagParser.parseTag(nbt);
        } catch (CommandSyntaxException e) {
            InfinityLib.instance.getLogger().error("Unable to parse NBT Data: \"{0}\".\nCause: {1}", nbt, e);
            return null;
        }
    }

    /**
     * Fetches a Collection of all BlockStates respecting the given attributes, if possible.
     *
     * @param element the registry name of the Block.
     * @param useTags to check if the element corresponds to a registry name in the tag registry instead
     * @param data a json string representation of the required BlockState Properties and their value
     * @param ignoredData a List of BlockState Properties to ignore
     * @return a Collection containing all matching BlockStates, or empty
     * optional.
     */
    @Nonnull
    public static final Collection<BlockState> fetchBlockStates(@Nullable String element, boolean useTags, String data, List<String> ignoredData) {
        // If null or empty return nothing.
        if (element == null || element.isEmpty()) {
            return Collections.emptyList();
        }

        // Split the element.
        final String[] parts = element.split(":");

        // If only 1 part, then assume is suffix.
        if (parts.length == 1) {
            return fetchBlockStates("minecraft", parts[0], useTags, data, ignoredData);
        } else if (parts.length == 2) {
            return fetchBlockStates(parts[0], parts[1], useTags, data, ignoredData);
        } else if (parts.length > 2) {
            InfinityLib.instance.getLogger().warn("Invalid stack identifier detected!\n\tGiven: \"{0}\"\n\tAssuming: \"{1}:{2}\"", element, parts[0], parts[1]);
            return fetchBlockStates(parts[0], parts[1], useTags, data, ignoredData);
        } else {
            throw new AssertionError("String.split() method worked incorrectly. This should be an impossible error.");
        }
    }

    @Nonnull
    public static final Collection<BlockState> fetchBlockStates(@Nonnull String prefix, @Nonnull String suffix,
                                                                boolean useTags, @Nullable String data, List<String> ignoredData) {
        // Validate
        Preconditions.checkNotNull(prefix, "A Block identifier must have a non-null prefix!");
        Preconditions.checkNotNull(suffix, "A Block identifier must have a non-null suffix!");

        // Test if the prefix is the special ore dict prefix.
        if (useTags) {
            return fetchBlockStatesFromTag(prefix, suffix, data, ignoredData);
        } else {
            return fetchBlockStatesNormal(prefix, suffix, data, ignoredData);
        }
    }

    @Nonnull
    private static Collection<BlockState> fetchBlockStatesNormal(@Nonnull String prefix, @Nonnull String suffix,  @Nullable String data, List<String> ignoredData) {
        // Step 0. Fetch the item.
        final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(prefix, suffix));

        // Step 1. Check that item is not null.
        if (block == null) {
            InfinityLib.instance.getLogger().error("Unable to resolve block: {0}:{1}.", prefix, suffix);
            return Collections.emptyList();
        }

        // Step 2. Return the block
        return block.getStateDefinition().getPossibleStates();  // TODO: filter based on data and ignoredData
    }

    @Nonnull
    private static Collection<BlockState> fetchBlockStatesFromTag(@Nonnull String prefix, @Nonnull String suffix, @Nullable String data, List<String> ignoredData) {
        // Do the thing.
        ITagManager<Block> registry = ForgeRegistries.BLOCKS.tags();
        TagKey<Block> key = registry.createTagKey(new ResourceLocation(prefix, suffix));
        return registry.getTag(key).stream()
                .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
                .filter(state -> true)  // TODO: filter based on data and ignoredData
                .collect(Collectors.toList());
    }

    /**
     * Fetches a Collection of all FluidStates respecting the given attributes, if possible.
     *
     * @param element the registry name of the Fluid.
     * @param useTags to check if the element corresponds to a registry name in the tag registry instead
     * @param data a json string representation of the required BlockState Properties and their value
     * @param ignoredData a List of BlockState Properties to ignore
     * @return a Collection containing all matching BlockStates, or empty
     * optional.
     */
    @Nonnull
    public static final Collection<FluidState> fetchFluidStates(@Nullable String element, boolean useTags, String data, List<String> ignoredData) {
        // If null or empty return nothing.
        if (element == null || element.isEmpty()) {
            return Collections.emptyList();
        }

        // Split the element.
        final String[] parts = element.split(":");

        // If only 1 part, then assume is suffix.
        if (parts.length == 1) {
            return fetchFluidStates("minecraft", parts[0], useTags, data, ignoredData);
        } else if (parts.length == 2) {
            return fetchFluidStates(parts[0], parts[1], useTags, data, ignoredData);
        } else if (parts.length > 2) {
            InfinityLib.instance.getLogger().warn("Invalid stack identifier detected!\n\tGiven: \"{0}\"\n\tAssuming: \"{1}:{2}\"", element, parts[0], parts[1]);
            return fetchFluidStates(parts[0], parts[1], useTags, data, ignoredData);
        } else {
            throw new AssertionError("String.split() method worked incorrectly. This should be an impossible error.");
        }
    }

    @Nonnull
    public static final Collection<FluidState> fetchFluidStates(@Nonnull String prefix, @Nonnull String suffix,
                                                                boolean useTags, @Nullable String data, List<String> ignoredData) {
        // Validate
        Preconditions.checkNotNull(prefix, "A Fluid identifier must have a non-null prefix!");
        Preconditions.checkNotNull(suffix, "A Fluid identifier must have a non-null suffix!");

        // Test if the prefix is the special ore dict prefix.
        if (useTags) {
            return fetchFluidStatesFromTag(prefix, suffix, data, ignoredData);
        } else {
            return fetchFluidStatesNormal(prefix, suffix, data, ignoredData);
        }
    }

    @Nonnull
    private static Collection<FluidState> fetchFluidStatesNormal(@Nonnull String prefix, @Nonnull String suffix,  @Nullable String data, List<String> ignoredData) {
        // Step 0. Fetch the item.
        final Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(prefix, suffix));

        // Step 1. Check that item is not null.
        if (fluid == null) {
            InfinityLib.instance.getLogger().error("Unable to resolve block: {0}:{1}.", prefix, suffix);
            return Collections.emptyList();
        }

        // Step 2. Return the Fluid States
        return fluid.getStateDefinition().getPossibleStates();  // TODO: filter based on data and ignoredData
    }

    @Nonnull
    private static Collection<FluidState> fetchFluidStatesFromTag(@Nonnull String prefix, @Nonnull String suffix, @Nullable String data, List<String> ignoredData) {
        // Do the thing.
        ITagManager<Fluid> registry = ForgeRegistries.FLUIDS.tags();
        TagKey<Fluid> key = registry.createTagKey(new ResourceLocation(prefix, suffix));
        return registry.getTag(key).stream()
                .flatMap(fluid -> fluid.getStateDefinition().getPossibleStates().stream())
                .filter(state -> true)  // TODO: filter based on data and ignoredData
                .collect(Collectors.toList());
    }

    /**
     * Dummy constructor to prevent instantiation of utility class.
     */
    private TagUtil() {
        // Nothing to see here @TehNut!
    }
}
