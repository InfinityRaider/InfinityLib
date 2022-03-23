package com.infinityraider.infinitylib.crafting.dynamictexture;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DynamicTextureIngredient extends Ingredient implements IDynamicTextureIngredient {
    public static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "dynamic_material");
    public static final Serializer SERIALIZER = new Serializer();

    private final BlockTagList tagList;

    protected DynamicTextureIngredient(ResourceLocation tagId) {
        this(new BlockTagList((tagId)));
    }

    protected DynamicTextureIngredient(BlockTagList tagList) {
        super(Stream.of(tagList));
        this.tagList = tagList;
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
        return material;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        return stack != null && !stack.isEmpty() && this.getTag().getAllElements().stream()
                .map(Block::asItem)
                .anyMatch(item -> stack.getItem().equals(item));
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    private static final class Serializer implements IInfIngredientSerializer<DynamicTextureIngredient> {
        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public DynamicTextureIngredient parse(FriendlyByteBuf buffer) {
            return new DynamicTextureIngredient(buffer.readResourceLocation());
        }

        @Override
        public DynamicTextureIngredient parse(JsonObject json) {
            if(!json.has("material")) {
                throw new JsonParseException("com.infinityraider.infinitylib.crafting.DynamicTextureIngredient requires a material element");
            }
            return new DynamicTextureIngredient(new ResourceLocation(GsonHelper.getAsString(json, "material")));
        }

        @Override
        public void write(FriendlyByteBuf buffer, DynamicTextureIngredient ingredient) {
            buffer.writeResourceLocation(ingredient.getTagId());
        }
    }

    public static final class BlockTagList implements Ingredient.IItemList {
        private final ResourceLocation tagId;

        private ITag<Block> tag;
        private Collection<ItemStack> stacks;

        public BlockTagList(ResourceLocation tagId) {
            this.tagId = tagId;
        }

        public ResourceLocation getTagId() {
            return tagId;
        }

        public ITag<Block> getTag() {
            if(this.tag == null) {
                this.tag = BlockTags.getCollection().get(this.getTagId());
            }
            return this.tag;
        }

        @Nonnull
        @Override
        public Collection<ItemStack> getStacks() {
            if(this.stacks == null) {
                ITag<Block> tag = this.getTag();
                if(tag == null) {
                    return ImmutableList.of();
                }
                this.stacks = tag.getAllElements().stream()
                        .map(Block::asItem)
                        .map(ItemStack::new)
                        .collect(Collectors.toList());
            }
            return this.stacks;
        }

        @Override
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("material", this.getTagId().toString());
            return json;
        }
    }
}
