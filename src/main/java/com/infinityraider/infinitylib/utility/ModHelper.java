package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.block.*;
import com.infinityraider.infinitylib.item.*;
import com.infinityraider.infinitylib.render.block.BlockRendererRegistry;
import com.infinityraider.infinitylib.item.IAutoRenderedItem;
import com.infinityraider.infinitylib.render.item.ItemRendererRegistry;
import com.infinityraider.infinitylib.entity.EntityRegistryEntry;
import net.minecraft.block.Block;
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

	private ModHelper() {
	}

	public void RegisterBlocksAndItems(InfinityMod mod) {
		//blocks
		ReflectionHelper.forEachIn(mod.getModBlockRegistry(), IInfinityBlock.class, (IInfinityBlock block) -> {
			if ((block instanceof Block) && block.isEnabled()) {
				mod.getLogger().debug("Registering Block: " + block.getInternalName());
				RegisterHelper.registerBlock((Block) block, mod.getModId(), block.getInternalName(), block.getItemBlockClass());
				for (String tag : block.getOreTags()) {
					OreDictionary.registerOre(tag, (Block) block);
				}
			}
		});

		//items
		ReflectionHelper.forEachIn(mod.getModItemRegistry(), IInfinityItem.class, (IInfinityItem item) -> {
			if ((item instanceof Item) && item.isEnabled()) {
				mod.getLogger().debug("Registering Item: " + item.getInternalName());
				RegisterHelper.registerItem((Item) item, mod.getModId(), item.getInternalName());
				for (String tag : item.getOreTags()) {
					OreDictionary.registerOre(tag, (Item) item);
				}
			}
		});

		//tile entities
		ReflectionHelper.forEachIn(mod.getModBlockRegistry(), IInfinityBlockWithTile.class, (IInfinityBlockWithTile block) -> {
			if (block.isEnabled()) {
				mod.getLogger().debug("Registering Tile for Block: " + block.getInternalName());
				TileEntity te = block.createNewTileEntity(null, 0);
				assert (te != null);
				GameRegistry.registerTileEntity(te.getClass(), mod.getModId().toLowerCase() + ":tile." + block.getInternalName());
			}
		});
	}

	public void registerRecipes(InfinityMod mod) {
		//blocks
		ReflectionHelper.forEachIn(mod.getModBlockRegistry(), IRecipeRegister.class, IRecipeRegister::registerRecipes);
		//items
		ReflectionHelper.forEachIn(mod.getModItemRegistry(), IRecipeRegister.class, IRecipeRegister::registerRecipes);
	}

	@SideOnly(Side.CLIENT)
	public void initRenderers(InfinityMod mod) {
		//blocks
		ReflectionHelper.forEachIn(mod.getModBlockRegistry(), IInfinityBlock.class, (IInfinityBlock block) -> {
			if (block.isEnabled() && (block instanceof ICustomRenderedBlock)) {
				BlockRendererRegistry.getInstance().registerCustomBlockRenderer((ICustomRenderedBlock) block);
			}
		});
		for (ICustomRenderedBlock block : BlockRendererRegistry.getInstance().getRegisteredBlocks()) {
			mod.getLogger().debug("registered custom renderer for " + block.getBlockModelResourceLocation());
		}
		//items
		ReflectionHelper.forEachIn(mod.getModItemRegistry(), IInfinityItem.class, (IInfinityItem item) -> {
			if ((item instanceof Item) && item.isEnabled()) {
				if (item instanceof IItemWithModel) {
                    for (Tuple<Integer, ModelResourceLocation> entry : ((IItemWithModel) item).getModelDefinitions()) {
                        ModelLoader.setCustomModelResourceLocation((Item) item, entry.getFirst(), entry.getSecond());
                    }
                }
				if (item instanceof IAutoRenderedItem) {
					ItemRendererRegistry.getInstance().registerCustomItemRendererAuto((Item & IAutoRenderedItem) item);
				} else if (item instanceof ICustomRenderedItem) {
					ItemRendererRegistry.getInstance().registerCustomItemRenderer((Item) item, ((ICustomRenderedItem) item).getRenderer());
				}
			}
		});
	}

    public void registerEntities(InfinityMod mod) {
        ReflectionHelper.forEachIn(mod.getModEntityRegistry(), EntityRegistryEntry.class, (EntityRegistryEntry entry) -> {
            if(entry.isEnabled()) {
                entry.register(mod);
                entry = null;
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public void registerEntitiesClient(InfinityMod mod) {
        ReflectionHelper.forEachIn(mod.getModEntityRegistry(), EntityRegistryEntry.class, (EntityRegistryEntry entry) -> {
            if(entry.isEnabled()) {
                entry.registerClient(mod);
            }
        });
    }
}
