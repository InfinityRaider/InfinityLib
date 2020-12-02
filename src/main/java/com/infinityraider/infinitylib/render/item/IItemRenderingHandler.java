package com.infinityraider.infinitylib.render.item;

import com.infinityraider.infinitylib.render.DefaultTransforms;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface IItemRenderingHandler {

    /**
     * Returns a list containing a ResourceLocation for every texture used to render this Item.
     * Passed textures are stitched to the Minecraft texture map and icons can be retrieved from them.
     * @return a list of ResourceLocations
     */
    List<RenderMaterial> getAllTextures();

    /**
     * Called to render the item
     * startDrawing() has already been called on the tessellator object.
     *
     * @param tessellator tessellator object to draw quads
     * @param world the world object
     * @param stack stack containing this block as an item
     * @param entity entity holding the stack
     */
    void renderItem(ITessellator tessellator, World world, ItemStack stack, LivingEntity entity);
    
    /**
     * Called to determine the cache key for this specific render.
     * 
     * @param world
     * @param stack
     * @param entity
     * @return 
     */
    default Object getItemQuadsCacheKey(World world, ItemStack stack, LivingEntity entity) {
        return stack.getItem();
    }

    default VertexFormat getVertexFormat() {
        return DefaultVertexFormats.BLOCK;
    }

    default DefaultTransforms.Transformer getPerspectiveTransformer() {
        return DefaultTransforms::getItemMatrix;
    }

}