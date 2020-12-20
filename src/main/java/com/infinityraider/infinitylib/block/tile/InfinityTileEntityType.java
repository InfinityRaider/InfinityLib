package com.infinityraider.infinitylib.block.tile;

import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class InfinityTileEntityType<T extends TileEntity> extends TileEntityType<T> implements IInfinityTileEntityType {
    private final String name;
    private final IRenderFactory<T> renderFactory;

    @OnlyIn(Dist.CLIENT)
    private ITileRenderer<T> renderer;

    private InfinityTileEntityType(String name, Supplier<? extends T> factory, Set<Block> validBlocks, IRenderFactory<T> renderFactory) {
        super(factory, validBlocks, null);
        this.name = name;
        this.renderFactory = renderFactory;
    }

    @Nonnull
    @Override
    public String getInternalName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public ITileRenderer<T> getRenderer() {
        if(this.renderer == null) {
            this.renderer = this.renderFactory.createRenderer();
        }
        return this.renderer;
    }

    public static <T extends TileEntity> Builder<T> builder(String name, Supplier<? extends T> factory) {
        return new Builder<>(name, factory);
    }

    public static final class Builder<T extends TileEntity> {
        private final String name;
        private final Supplier<? extends T> factory;
        private final Set<Block> blocks;

        private IRenderFactory<T> renderFactory;

        private Builder(String name, Supplier<? extends T> factory) {
            this.name = name;
            this.factory = factory;
            this.blocks = Sets.newIdentityHashSet();
            this.renderFactory = () -> null;
        }

        public Builder<T> addBlock(Block block) {
            this.blocks.add(block);
            return this;
        }

        public Builder<T> addBlocks(Collection<Block> block) {
            this.blocks.addAll(block);
            return this;
        }

        public Builder<T> addBlocks(Block... blocks) {
            Arrays.stream(blocks).forEach(this::addBlock);
            return this;
        }

        public Builder<T> setRenderFactory(IRenderFactory<T> factory) {
            this.renderFactory = factory;
            return this;
        }

        public InfinityTileEntityType<T> build() {
            return new InfinityTileEntityType<T>(this.name, this.factory, this.blocks, this.renderFactory);
        }
    }

    public interface IRenderFactory<T extends TileEntity> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        ITileRenderer<T> createRenderer();
    }
}
