package com.infinityraider.infinitylib;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.config.Config;
import com.infinityraider.infinitylib.crafting.RecipeSerializers;
import com.infinityraider.infinitylib.modules.Module;
import com.infinityraider.infinitylib.network.*;
import com.infinityraider.infinitylib.proxy.ClientProxy;
import com.infinityraider.infinitylib.proxy.IProxy;
import com.infinityraider.infinitylib.proxy.ServerProxy;
import com.infinityraider.infinitylib.reference.Reference;
import com.infinityraider.infinitylib.render.model.InfModelLoader;
import com.infinityraider.infinitylib.render.model.InfModelLoaderComposite;
import com.infinityraider.infinitylib.render.model.InfModelLoaderDynamicTexture;
import com.infinityraider.infinitylib.sound.MessagePlaySound;
import com.infinityraider.infinitylib.sound.MessageStopSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod(Reference.MOD_ID)
public class InfinityLib extends InfinityMod<IProxy, Config> {
    public static InfinityLib instance;

    public InfinityLib() {
        super();
    }

    @Override
    public String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    protected void onModConstructed() {
        instance = this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected IProxy createClientProxy() {
        return new ClientProxy();
    }

    @Override
    @OnlyIn(Dist.DEDICATED_SERVER)
    protected IProxy createServerProxy() {
        return new ServerProxy();
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageAutoSyncTileField.class);
        wrapper.registerMessage(MessageRenderUpdate.class);
        wrapper.registerMessage(MessageSetEntityDead.class);
        wrapper.registerMessage(MessageSyncTile.class);
        wrapper.registerMessage(MessagePlaySound.class);
        wrapper.registerMessage(MessageStopSound.class);
        Module.getActiveModules().stream().sorted().forEach(m -> m.registerMessages(wrapper));
    }

    @Override
    public RecipeSerializers getModRecipeSerializerRegistry() {
        return RecipeSerializers.getInstance();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<InfModelLoader<?>> getModModelLoaders() {
        return ImmutableList.of(
                InfModelLoaderComposite.getInstance(),
                InfModelLoaderDynamicTexture.getInstance()
        );
    }
}