package com.infinityraider.infinitylib.crafting.dynamictexture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public class DynamicTextureParentIngredient extends Ingredient implements IDynamicTextureIngredient {
    public static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "dynamic_material_parent");
    public static final Serializer SERIALIZER = new Serializer();

    private final ItemStack parent;
    private final DynamicTextureIngredient.BlockTagList tagList;

    protected DynamicTextureParentIngredient(ItemStack parent, ResourceLocation tagId) {
        this(parent, new DynamicTextureIngredient.BlockTagList((tagId)));
    }

    protected DynamicTextureParentIngredient(ItemStack parent, DynamicTextureIngredient.BlockTagList tagList) {
        super(Stream.of(new Ingredient.ItemValue(parent)));
        this.parent = parent;
        this.tagList = tagList;
    }

    public ItemStack getParent() {
        return this.parent;
    }

    @Override
    public ResourceLocation getTagId() {
        return this.tagList.getTagId();
    }

    @Override
    public ITag<Block> getTag() {
        return this.tagList.getTag();
    }

    @Override
    public ItemStack asStackWithMaterial(ItemStack material) {
        ItemStack stack = this.getParent().copy();
        if(stack.getItem() instanceof BlockItemDynamicTexture) {
            ((BlockItemDynamicTexture) stack.getItem()).setMaterial(stack, material);
        }
        return stack;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
       return stack != null
               && !stack.isEmpty()
               && stack.getItem() instanceof BlockItemDynamicTexture;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    private static final class Serializer implements IInfIngredientSerializer<DynamicTextureParentIngredient> {
        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public DynamicTextureParentIngredient parse(FriendlyByteBuf buffer) {
            return new DynamicTextureParentIngredient(buffer.readItem(), buffer.readResourceLocation());
        }

        @Override
        public DynamicTextureParentIngredient parse(JsonObject json) {
            if(!json.has("parent")) {
                throw new JsonParseException("com.infinityraider.infinitylib.crafting.DynamicTextureParentIngredient requires a parent element");
            }
            if(!json.has("material")) {
                throw new JsonParseException("com.infinityraider.infinitylib.crafting.DynamicTextureParentIngredient requires a material element");
            }
            ResourceLocation rl = new ResourceLocation(JSONUtils.getString(json, "parent"));
            Item parent = Optional.ofNullable(ForgeRegistries.ITEMS.getValue(rl)).orElseThrow(() ->
                    new JsonSyntaxException("Unknown item '" + rl + "'"));
            return new DynamicTextureParentIngredient(new ItemStack(parent), new ResourceLocation(JSONUtils.getString(json, "material")));
        }

        @Override
        public void write(FriendlyByteBuf buffer, DynamicTextureParentIngredient ingredient) {
            buffer.writeItemStack(ingredient.getParent(), false);
            buffer.writeResourceLocation(ingredient.getTagId());
        }
    }
}
