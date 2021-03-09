package com.infinityraider.infinitylib.crafting.dynamictexture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.infinityraider.infinitylib.InfinityLib;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public class DynamicTextureParentIngredient extends Ingredient {
    public static final ResourceLocation ID = new ResourceLocation(InfinityLib.instance.getModId(), "dynamic_material_parent");
    public static final Serializer SERIALIZER = new Serializer();

    private final ItemStack parent;

    protected DynamicTextureParentIngredient(ItemStack parent) {
        super(Stream.of(new Ingredient.SingleItemList(parent)));
        this.parent = parent;
    }

    public ItemStack getParent() {
        return this.parent;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
       return stack != null
               && !stack.isEmpty()
               && stack.getItem() instanceof BlockItemDynamicTexture;
    }

    private static final class Serializer implements IInfIngredientSerializer<DynamicTextureParentIngredient> {
        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public DynamicTextureParentIngredient parse(PacketBuffer buffer) {
            return new DynamicTextureParentIngredient(buffer.readItemStack());
        }

        @Override
        public DynamicTextureParentIngredient parse(JsonObject json) {
            if(!json.has("parent")) {
                throw new JsonParseException("com.infinityraider.infinitylib.crafting.DynamicTextureParentIngredient requires a parent element");
            }
            ResourceLocation rl = new ResourceLocation(JSONUtils.getString(json, "parent"));
            Item parent = Optional.ofNullable(ForgeRegistries.ITEMS.getValue(rl)).orElseThrow(() ->
                    new JsonSyntaxException("Unknown item '" + rl + "'"));
            return new DynamicTextureParentIngredient(new ItemStack(parent));
        }

        @Override
        public void write(PacketBuffer buffer, DynamicTextureParentIngredient ingredient) {
            buffer.writeItemStack(ingredient.getParent());
        }
    }
}
