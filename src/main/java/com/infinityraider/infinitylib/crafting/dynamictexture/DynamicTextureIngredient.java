package com.infinityraider.infinitylib.crafting.dynamictexture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DynamicTextureIngredient extends Ingredient {
    public static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "dynamic_texture");
    public static final Serializer SERIALIZER = new Serializer();

    private final BlockTagList tagList;

    protected DynamicTextureIngredient(ITag<Block> tag) {
        this(new BlockTagList(tag));
    }

    protected DynamicTextureIngredient(BlockTagList tagList) {
        super(Stream.of(tagList));
        this.tagList = tagList;
    }

    public ITag<Block> getTag() {
        return this.tagList.getTag();
    }

    private static final class Serializer implements IInfIngredientSerializer<DynamicTextureIngredient> {
        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public DynamicTextureIngredient parse(PacketBuffer buffer) {
            boolean flag = buffer.readBoolean();
            ITag<Block> tag = null;
            if(flag) {
                ResourceLocation rl = buffer.readResourceLocation();
                tag = BlockTags.getCollection().getTagByID(rl);
            }
            return tag == null ? null : new DynamicTextureIngredient(tag);
        }

        @Override
        public DynamicTextureIngredient parse(JsonObject json) {
            if(!json.has("material")) {
                throw new JsonParseException("com.infinityraider.infinitylib.crafting.DynamicTextureIngredient requires a material element");
            }
            ResourceLocation rl = new ResourceLocation(JSONUtils.getString(json, "material"));
            ITag<Block> tag = BlockTags.getCollection().getTagByID(rl);
            return new DynamicTextureIngredient(tag);
        }

        @Override
        public void write(PacketBuffer buffer, DynamicTextureIngredient ingredient) {
            ResourceLocation rl = BlockTags.getCollection().getDirectIdFromTag(ingredient.getTag());
            boolean flag = rl != null;
            buffer.writeBoolean(flag);
            if(flag) {
                buffer.writeResourceLocation(rl);
            }
        }
    }

    private static final class BlockTagList implements Ingredient.IItemList {
        private final ITag<Block> tag;

        public BlockTagList(ITag<Block> tag) {
            this.tag = tag;
        }

        public ITag<Block> getTag() {
            return this.tag;
        }

        @Nonnull
        @Override
        public Collection<ItemStack> getStacks() {
            return this.getTag().getAllElements().stream()
                    .map(Block::asItem)
                    .map(ItemStack::new)
                    .collect(Collectors.toList());
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("material", TagCollectionManager.getManager().getBlockTags()
                    .getValidatedIdFromTag(this.getTag())
                    .toString());
            return jsonobject;
        }
    }
}
