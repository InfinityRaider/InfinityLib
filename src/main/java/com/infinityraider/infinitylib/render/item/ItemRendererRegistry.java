package com.infinityraider.infinitylib.render.item;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ItemRendererRegistry  {

	private static final ItemRendererRegistry INSTANCE = new ItemRendererRegistry();

	public static ItemRendererRegistry getInstance() {
		return INSTANCE;
	}

	private final Map<ResourceLocation, ItemRenderer> renderers;

	private ItemRendererRegistry() {
		this.renderers = new HashMap<>();
		//ModelLoaderRegistry.registerLoader(this);		//TODO
	}

	public void registerCustomItemRenderer(Item item, IItemRenderingHandler handler) {
		final ModelResourceLocation itemModel = new ModelResourceLocation(item.getRegistryName(), "inventory");
		final ItemRenderer instance = new ItemRenderer(handler);
		//ModelLoader.setCustomMeshDefinition(item, stack -> itemModel);	//TODO
		renderers.put(itemModel, instance);
	}

}
