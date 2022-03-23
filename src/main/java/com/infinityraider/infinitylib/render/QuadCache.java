package com.infinityraider.infinitylib.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class QuadCache {
    private final Map<Direction, SafeQuadStore> dirQuads;
    private final SafeQuadStore nullQuads;

    public QuadCache(Function<Direction, List<BakedQuad>> quadBaker) {
        this.dirQuads = Maps.newConcurrentMap();
        Arrays.stream(Direction.values()).forEach(face -> dirQuads.put(face, new SafeQuadStore(face, quadBaker)));
        this.nullQuads = new SafeQuadStore(null, quadBaker);
    }

    public List<BakedQuad> getQuads(@Nullable Direction face) {
        return face == null ? this.nullQuads.getQuads() : this.dirQuads.get(face).getQuads();
    }

    private static final class SafeQuadStore {
        private final Direction face;
        private final Function<Direction, List<BakedQuad>> quadBaker;

        private List<BakedQuad> quads;
        private boolean baking;

        private SafeQuadStore(@Nullable Direction face, Function<Direction, List<BakedQuad>> quadBaker) {
            this.face = face;
            this.quadBaker = quadBaker;
        }

        @Nullable
        public Direction getFace() {
            return this.face;
        }

        public List<BakedQuad> getQuads() {
            if (this.quads == null) {
                if (this.baking) {
                    return ImmutableList.of();
                } else {
                    this.baking = true;
                    this.quads = ImmutableList.copyOf(this.quadBaker.apply(this.getFace()));
                }
            }
            return this.quads;
        }
    }
}
