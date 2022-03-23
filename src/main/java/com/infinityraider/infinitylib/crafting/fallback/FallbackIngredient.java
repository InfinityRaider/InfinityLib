package com.infinityraider.infinitylib.crafting.fallback;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class FallbackIngredient extends Ingredient {
    public static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "fallback");
    public static final Serializer SERIALIZER = new Serializer();

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
            if(this.getTag() != null && this.getTag().getAllElements().size() > 0) {
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
        JsonObject rootJson = new JsonObject();
        JsonObject ingredientJson = new JsonObject();
        ResourceLocation rl = ItemTags.getCollection().getDirectIdFromTag(this.getTag());
        ingredientJson.addProperty("tag", rl.toString());
        ingredientJson.add("fallback", this.getFallback().serialize());
        rootJson.add("ingredient", ingredientJson);
        rootJson.addProperty("type", InfinityLib.instance.getModId() + ":fallback");
        return rootJson;
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
    }

    private static final class Serializer implements IInfIngredientSerializer<FallbackIngredient> {
        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public FallbackIngredient parse(FriendlyByteBuf buffer) {
            boolean flag = buffer.readBoolean();
            ITag<Item> tag = null;
            if(flag) {
                ResourceLocation rl = buffer.readResourceLocation();
                tag = ItemTags.getCollection().getTagByID(rl);
            }
            Ingredient fallback = Ingredient.read(buffer);
            return new FallbackIngredient(tag, fallback);
        }

        @Override
        public FallbackIngredient parse(JsonObject json) {
            if(!json.has("ingredient")) {
                throw new JsonParseException("com.infinityraider.infinitylib.crafting.FallBackIngredient requires an ingredient element");
            }
            JsonElement element = json.get("ingredient");
            if(!(element instanceof JsonObject)) {
                throw new JsonParseException("com.infinityraider.infinitylib.crafting.FallBackIngredient expected an object for ingredient");
            }
            JsonObject ingredientJson = (JsonObject) element;
            if(!ingredientJson.has("tag")) {
                throw new JsonParseException("com.infinityraider.infinitylib.crafting.FallBackIngredient requires a tag element");
            }
            if(!ingredientJson.has("fallback")) {
                throw new JsonParseException("com.infinityraider.infinitylib.crafting.FallBackIngredient requires a fallback element");
            }
            ResourceLocation rl = new ResourceLocation(JSONUtils.getString(ingredientJson, "tag"));
            ITag<Item> tag = ItemTags.getCollection().getTagByID(rl);
            Ingredient fallback = CraftingHelper.getIngredient(ingredientJson.get("fallback"));
            return new FallbackIngredient(tag, fallback);
        }

        @Override
        public void write(FriendlyByteBuf buffer, FallbackIngredient ingredient) {
            ResourceLocation rl = ItemTags.getCollection().getDirectIdFromTag(ingredient.getTag());
            boolean flag = rl != null;
            buffer.writeBoolean(flag);
            if(flag) {
                buffer.writeResourceLocation(rl);
            }
            ingredient.getFallback().write(buffer);
        }
    }
}
