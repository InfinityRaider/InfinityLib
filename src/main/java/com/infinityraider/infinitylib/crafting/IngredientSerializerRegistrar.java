package com.infinityraider.infinitylib.crafting;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.Map;

public class IngredientSerializerRegistrar {
    private static final IngredientSerializerRegistrar INSTANCE = new IngredientSerializerRegistrar();

    public static IngredientSerializerRegistrar getInstance() {
        return INSTANCE;
    }

    // all serializers
    private final Map<ResourceLocation, IInfIngredientSerializer<?>> ingredientSerializers = Maps.newConcurrentMap();

    private IngredientSerializerRegistrar() {
    }

    public void registerSerializer(IInfIngredientSerializer<?> serializer) {
       ingredientSerializers.putIfAbsent(serializer.getId(), serializer);
    }

    public void registerSerializers() {
        ingredientSerializers.values().forEach(serializer -> CraftingHelper.register(serializer.getId(), serializer));
    }
}
