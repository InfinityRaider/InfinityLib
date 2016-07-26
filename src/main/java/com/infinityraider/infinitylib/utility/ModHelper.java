package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.ICustomRenderedBlock;
import com.infinityraider.infinitylib.item.ICustomRenderedItem;
import com.infinityraider.infinitylib.item.IItemWithRecipe;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.network.NetworkWrapper;
import com.infinityraider.infinitylib.render.block.BlockRendererRegistry;
import com.infinityraider.infinitylib.render.item.ItemRendererRegistry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ModHelper {
    private static final ModHelper INSTANCE = new ModHelper();

    public static ModHelper getInstance() {
        return INSTANCE;
    }

    private ModHelper() {}

    public void RegisterBlocksAndItems(InfinityMod mod) {
        //blocks
        LogHelper.debug("Starting Block Registration...");
        ReflectionHelper.forEachIn(mod.getModBlockRegistry(), BlockBase.class, (BlockBase block) -> {
            if(block.isEnabled()) {
                LogHelper.debug("Registering Block: " + block.getInternalName());
                RegisterHelper.registerBlock(block, mod.getModId(), block.getInternalName(), block.getItemBlockClass());
                for(String tag: block.getOreTags()) {
                    OreDictionary.registerOre(tag, block);
                }
            }
        });
        LogHelper.debug("Finished Block Registration!");

        //items
        LogHelper.debug("Starting Item Registration...");
        ReflectionHelper.forEachIn(mod.getModItemRegistry(), ItemBase.class, (ItemBase item) -> {
            if(item.isEnabled()) {
                LogHelper.debug("Registering Item: " + item.getInternalName());
                RegisterHelper.registerItem(item, mod.getModId(), item.getInternalName());
                for(String tag: item.getOreTags()) {
                    OreDictionary.registerOre(tag, item);
                }
            }
        });
        LogHelper.debug("Finished Item Registration!");

        //tile entities
        LogHelper.debug("Starting Tile Registration...");
        ReflectionHelper.forEachIn(mod.getModBlockRegistry(), BlockBaseTile.class, (BlockBaseTile block) -> {
            if(block.isEnabled()) {
                LogHelper.debug("Registering Tile for Block: " + block.getInternalName());
                TileEntity te = block.createNewTileEntity(null, 0);
                assert (te != null);
                GameRegistry.registerTileEntity(te.getClass(), mod.getModId().toLowerCase() + ":tile." + block.getInternalName());
            }
        });
        LogHelper.debug("Finished Tile Registration!");
        //network messages
        mod.registerMessages(NetworkWrapper.getInstance());
    }

    public void registerRecipes(InfinityMod mod) {
        LogHelper.debug("Starting Recipe Registration...");
        //blocks
        ReflectionHelper.forEachIn(mod.getModBlockRegistry(), BlockBase.class, (BlockBase block) -> {
            if(block.isEnabled() && (block instanceof IItemWithRecipe)) {
                ((IItemWithRecipe) block).getRecipes().forEach(GameRegistry::addRecipe);
            }
        });
        //items
        ReflectionHelper.forEachIn(mod.getModBlockRegistry(), ItemBase.class, (ItemBase item) -> {
            if(item.isEnabled() && (item instanceof IItemWithRecipe)) {
                ((IItemWithRecipe) item).getRecipes().forEach(GameRegistry::addRecipe);
            }
        });
        LogHelper.debug("Finished Recipe Registration!");
    }

    @SideOnly(Side.CLIENT)
    public void initRenderers(InfinityMod mod) {
        LogHelper.debug("Starting Renderer Registration...");
        //blocks
        ReflectionHelper.forEachIn(mod.getModBlockRegistry(), BlockBase.class, (BlockBase block) -> {
            if(block.isEnabled() && (block instanceof ICustomRenderedBlock)) {
                BlockRendererRegistry.getInstance().registerCustomBlockRenderer((ICustomRenderedBlock) block);
            }
        });
        for(ICustomRenderedBlock block : BlockRendererRegistry.getInstance().getRegisteredBlocks()) {
            LogHelper.debug("registered custom renderer for " + block.getBlockModelResourceLocation());
        }
        //items
        ReflectionHelper.forEachIn(mod.getModItemRegistry(), ItemBase.class, (ItemBase item) -> {
            if (item.isEnabled()) {
                for (Tuple<Integer, ModelResourceLocation> entry : item.getModelDefinitions()) {
                    ModelLoader.setCustomModelResourceLocation(item, entry.getFirst(), entry.getSecond());
                }
                if (item instanceof ICustomRenderedItem) {
                    ItemRendererRegistry.getInstance().registerCustomItemRenderer((ICustomRenderedItem<? extends Item>) item);
                }
            }
        });
        for (ICustomRenderedItem item : ItemRendererRegistry.getInstance().getRegisteredItems()) {
            LogHelper.debug("Registered custom renderer for " + item.getItemModelResourceLocation());
        }
        LogHelper.debug("Finished Renderer Registration!");
    }
}
