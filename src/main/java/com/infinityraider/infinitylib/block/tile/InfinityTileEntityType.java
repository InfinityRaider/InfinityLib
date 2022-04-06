package com.infinityraider.infinitylib.block.tile;

import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class InfinityTileEntityType<T extends BlockEntity> extends BlockEntityType<T> implements IInfinityTileEntityType {
    private final String name;
    private final IRenderFactory<T> renderFactory;
    private final boolean ticking;

    @OnlyIn(Dist.CLIENT)
    private ITileRenderer<T> renderer;

    private InfinityTileEntityType(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory, Set<Block> validBlocks, boolean ticking, IRenderFactory<T> renderFactory) {
        super(factory, validBlocks, null);
        this.name = name;
        this.ticking = ticking;
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
    public boolean isTicking() {
        return this.ticking;
    }

    @Override
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public ITileRenderer<T> getRenderer() {
        if (this.renderer == null) {
            this.renderer = this.renderFactory.createRenderer();
        }
        return this.renderer;
    }

    public static <T extends BlockEntity> Builder<T> builder(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory) {
        return new Builder<T>(name, factory);
    }

    public static final class Builder<T extends BlockEntity> {
        private final String name;
        private final BlockEntityType.BlockEntitySupplier<? extends T> factory;
        private final Set<Block> blocks;

        private boolean ticking;
        private IRenderFactory<T> renderFactory;

        private Builder(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory) {
            this.name = name;
            this.factory = factory;
            this.blocks = Sets.newIdentityHashSet();
            this.ticking = false;
            this.renderFactory = noRenderer();
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

        public Builder<T> setTicking() {
            this.ticking = true;
            return this;
        }

        public Builder<T> setRenderFactory(IRenderFactory<T> factory) {
            this.renderFactory = factory;
            return this;
        }

        public InfinityTileEntityType<T> build() {
            return new InfinityTileEntityType<T>(this.name, this.factory, this.blocks, this.ticking, this.renderFactory);
        }
    }

    public static <T extends BlockEntity> IRenderFactory<T> noRenderer() {
        return new NoRenderFactory<>();
    }

    public interface IRenderFactory<T extends BlockEntity> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        ITileRenderer<T> createRenderer();
    }

    private static final class NoRenderFactory<T extends BlockEntity> implements IRenderFactory<T> {
        @Override
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public ITileRenderer<T> createRenderer() {
            return null;
        }
    }
}
