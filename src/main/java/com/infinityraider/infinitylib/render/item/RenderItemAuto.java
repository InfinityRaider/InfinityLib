/*
 */
package com.infinityraider.infinitylib.render.item;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.utility.LogHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderItemAuto<T extends ItemBase & IAutoRenderedItem> implements IItemRenderingHandler {

	private final Map<String, List<BakedQuad>> models = new ConcurrentHashMap<>();
	private final T item;

	public RenderItemAuto(T item) {
		this.item = item;
	}

	public T getItem() {
		return item;
	}

	@Override
	public List<ResourceLocation> getAllTextures() {
		return item.getAllTextures();
	}

	@Override
	public void renderItem(ITessellator tessellator, World world, Item item, ItemStack stack,
						   EntityLivingBase entity, ItemCameraTransforms.TransformType type, VertexFormat format) {
		final String id = this.item.getModelId(stack);
		List<BakedQuad> model = models.get(id);
		if (model == null) {
			LogHelper.debug("Baking Clipping Model: " + id  + " !");
			model = ItemQuadGenerator.generateItemQuads(
					DefaultVertexFormats.ITEM,
					tessellator::getIcon,
					this.item.getBaseTexture(stack),
					this.item.getOverlayTextures(stack)
			);
			models.put(id, model);
		}
		tessellator.addQuads(model);
	}
}
