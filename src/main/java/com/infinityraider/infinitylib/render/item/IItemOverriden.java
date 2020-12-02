package com.infinityraider.infinitylib.render.item;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface IItemOverriden {

	BakedInfItemSubModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity);

	@OnlyIn(Dist.CLIENT)
	final class Wrapper extends ItemOverrideList {

		public final IItemOverriden override;

		public Wrapper(IItemOverriden override) {
			this.override = override;
		}

		@Override
		public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
			return this.override.handleItemState(originalModel, stack, world, entity);
		}

	}

}
