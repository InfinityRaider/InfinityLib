package com.infinityraider.infinitylib;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.config.Config;
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
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.InfinityLibContentRegistry;
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
    @OnlyIn(Dist.CLIENT)
    public List<InfModelLoader<?>> getModModelLoaders() {
        return ImmutableList.of(
                InfModelLoaderComposite.getInstance(),
                InfModelLoaderDynamicTexture.getInstance()
        );
    }

    @Override
    public ModContentRegistry getModBlockRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModTileRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModItemRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModFluidRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModBiomeRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModEnchantmentRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModEntityRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModEffectRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModPotionTypeRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModSoundRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModParticleRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModContainerRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModRecipeSerializerRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }

    @Override
    public ModContentRegistry getModLootModifierSerializerRegistry() {
        return InfinityLibContentRegistry.getInstance();
    }
}