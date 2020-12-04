package com.infinityraider.infinitylib.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.infinitylib.InfinityLib;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class FallbackIngredient extends Ingredient {
    private final ITag<Item> tag;
    private final Ingredient fallback;

    private Ingredient ingredient;

    public FallbackIngredient(ITag<Item> tag, Ingredient fallback) {
        super(Stream.empty());
        this.tag = tag;
        this.fallback = fallback;
    }

    protected ITag<Item> getTag() {
        return this.tag;
    }

    protected Ingredient getFallback() {
        return this.fallback;
    }

    private Ingredient getActualIngredient() {
        if(this.ingredient == null) {
            if(getTag().getAllElements().size() > 0) {
                this.ingredient = Ingredient.fromTag(this.getTag());
            } else if (this.getFallback().hasNoMatchingItems()) {
                return EMPTY;
            } else {
                this.ingredient = getFallback();
            }
        }
        return this.ingredient;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return this.getActualIngredient().getMatchingStacks();
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        return this.getActualIngredient().test(stack);
    }

    @Override
    public IntList getValidItemStacksPacked() {
        return this.getActualIngredient().getValidItemStacksPacked();
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        ResourceLocation rl = ItemTags.getCollection().getDirectIdFromTag(this.getTag());
        json.addProperty("tag", rl.toString());
        json.add("fallback", this.getFallback().serialize());
        return json;
    }

    @Override
    public boolean hasNoMatchingItems() {
        return this.getActualIngredient().hasNoMatchingItems();
    }

    @Override
    protected void invalidate() {
        this.ingredient = null;
    }

    @Override
    public boolean isSimple() {
        return this.getActualIngredient().isSimple();
    }

    @Override
    public IIngredientSerializer<FallbackIngredient> getSerializer() {
        return SERIALIZER;
    }

    public static void registerSerializer() {
        CraftingHelper.register(new ResourceLocation(InfinityLib.instance.getModId(), "fallback"), SERIALIZER);
    }

    private static final Serializer SERIALIZER = new Serializer();

    private static final class Serializer implements IIngredientSerializer<FallbackIngredient> {

        @Override
        public FallbackIngredient parse(PacketBuffer buffer) {
            ResourceLocation rl = buffer.readResourceLocation();
            ITag<Item> tag = ItemTags.getCollection().getTagByID(rl);
            Ingredient fallback = Ingredient.read(buffer);
            return new FallbackIngredient(tag, fallback);
        }

        @Override
        public FallbackIngredient parse(JsonObject json) {
            if(!json.has("tag")) {
                throw new JsonParseException("com.infinityraider.infinitylib.crafting.FallBackIngredient requires a tag element");
            }
            if(!json.has("fallback")) {
                throw new JsonParseException("com.infinityraider.infinitylib.crafting.FallBackIngredient requires a fallback element");
            }
            ResourceLocation rl = new ResourceLocation(JSONUtils.getString(json, "tag"));
            ITag<Item> tag = ItemTags.getCollection().getTagByID(rl);
            Ingredient fallback = CraftingHelper.getIngredient(json.get("fallback"));
            return new FallbackIngredient(tag, fallback);
        }

        @Override
        public void write(PacketBuffer buffer, FallbackIngredient ingredient) {
            ResourceLocation rl = ItemTags.getCollection().getDirectIdFromTag(ingredient.getTag());
            buffer.writeResourceLocation(rl);
            ingredient.getFallback().write(buffer);
        }
    }
}
