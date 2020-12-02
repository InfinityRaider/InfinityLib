package com.infinityraider.infinitylib.render.block;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.block.ICustomRenderedBlockWithTile;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BlockRendererRegistry {

    private static final BlockRendererRegistry INSTANCE = new BlockRendererRegistry();

    public static BlockRendererRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, BlockRenderer>renderers;
    private final List<ICustomRenderedBlock> blocks;

    private BlockRendererRegistry() {
        this.renderers = new HashMap<>();
        this.blocks = new ArrayList<>();
        //ModelLoaderRegistry.registerLoader(new ResourceLocation(InfinityLib.instance.getModId(),"blocks"),this); //TODO
    }

    public List<ICustomRenderedBlock> getRegisteredBlocks() {
        return ImmutableList.copyOf(blocks);
    }

    public void registerCustomBlockRenderer(TileEntityRendererDispatcher dispatcher, ICustomRenderedBlock customRenderedBlock) {
        //TODO
    }

    @SuppressWarnings("unchecked")
    private void registerBlockRenderer(ICustomRenderedBlock customRenderedBlock) {
        IBlockRenderingHandler renderer = customRenderedBlock.getRenderer();
        if (renderer != null) {
            BlockRenderer instance = new BlockRenderer(renderer);
            ModelResourceLocation blockModel = customRenderedBlock.getBlockModelResourceLocation();
            //static rendering
            renderers.put(blockModel, instance);
            //inventory rendering
            registerInventoryRendering(renderer, blockModel, instance);
            blocks.add(customRenderedBlock);
        }
    }

    @SuppressWarnings("unchecked")
    private void registerTileRenderer(TileEntityRendererDispatcher dispatcher, ICustomRenderedBlockWithTile<? extends TileEntityBase> customRenderedBlock) {
        ITileRenderingHandler renderer = customRenderedBlock.getRenderer();
        if (renderer != null) {
            BlockWithTileRenderer instance = new BlockWithTileRenderer(renderer);
            ModelResourceLocation blockModel = customRenderedBlock.getBlockModelResourceLocation();
            //static rendering
            if (renderer.hasStaticRendering()) {
                renderers.put(blockModel, instance);
            }
            //dynamic rendering
            TileEntity tile = renderer.getTileEntity();
            if (renderer.hasDynamicRendering() && tile != null) {
                //ClientRegistry.bindTileEntitySpecialRenderer(tile.getClass(), new TesrWrapper<>(dispatcher, instance));
                //TODO: bind TESR
            }
            //inventory rendering
            registerInventoryRendering(renderer, blockModel, instance);
            blocks.add(customRenderedBlock);
        }
    }

    private void registerInventoryRendering(@Nonnull IBlockRenderingHandler renderer, @Nonnull ModelResourceLocation loc, BlockRenderer model) {
        // Validate.
        Preconditions.checkNotNull(renderer);
        Preconditions.checkNotNull(loc);
        Preconditions.checkNotNull(model);

        // Do the thing.
        if (renderer.doInventoryRendering()) {
            ModelResourceLocation itemModel = new ModelResourceLocation(loc.getNamespace() + ":" + loc.getPath(), "inventory");
            renderers.put(itemModel, model);
            // Get the item.
            final Item item = Item.getItemFromBlock(renderer.getBlock());
            // Check that Item exists.
            if (item != null) { // HACK because something, I don't know what, but something changed.
                //ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(renderer.getBlock()), stack -> itemModel);
                //TODO: register inv rendering
            }
        }
    }
}
