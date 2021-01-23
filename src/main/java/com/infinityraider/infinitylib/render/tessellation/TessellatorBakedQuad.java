package com.infinityraider.infinitylib.render.tessellation;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Helper class to construct vertices
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class TessellatorBakedQuad extends TessellatorAbstractBase {

    /**
     * Draw mode when no vertices are being constructed
     */
    public static final int DRAW_MODE_NOT_DRAWING = -1;
    /**
     * Draw mode when vertices are being constructed for quads
     */
    public static final int DRAW_MODE_QUADS = 4;

    /**
     * Currently constructed quads
     */
    private final List<BakedQuad> quads;
    /**
     * Currently constructed vertices
     */
    private final List<VertexData> vertexData;

    /**
     * Current drawing mode
     */
    private int drawMode;
    /**
     * Face being drawn
     */
    @Nonnull
    private Face face;
    /**
     * Icon currently drawing with
     */
    private TextureAtlasSprite icon;
    /**
     * Texture function
     */
    private Function<RenderMaterial, TextureAtlasSprite> textureFunction;

    /**
     * Private constructor
     */
    public TessellatorBakedQuad() {
        super();
        this.quads = new ArrayList<>();
        this.vertexData = new ArrayList<>();
        this.drawMode = DRAW_MODE_NOT_DRAWING;
        this.face = Face.NONE;
    }

    /**
     * Sub delegated method call of the startDrawingQuads() method to ensure
     * correct call chain
     */
    @Override
    protected void onStartDrawingQuadsCall() {
        this.startDrawing(DRAW_MODE_QUADS);
    }

    /**
     * Method to start constructing vertices
     *
     * @param mode draw mode
     */
    public void startDrawing(int mode) {
        if (drawMode == DRAW_MODE_NOT_DRAWING) {
            this.drawMode = mode;
        } else {
            throw new RuntimeException("ALREADY CONSTRUCTING VERTICES");
        }
    }

    /**
     * Method to get all quads constructed
     *
     * @return list of quads, may be empty but never null
     */
    @Override
    public ImmutableList<BakedQuad> getQuads() {
        return ImmutableList.copyOf(this.quads);
    }

    /**
     * Sub delegated method call of the draw() method to ensure correct call
     * chain
     */
    @Override
    protected void onDrawCall() {
        if (drawMode != DRAW_MODE_NOT_DRAWING) {
            quads.clear();
            vertexData.clear();
            this.drawMode = DRAW_MODE_NOT_DRAWING;
            this.textureFunction = null;
            this.face = Face.NONE;
        } else {
            throw new RuntimeException("NOT CONSTRUCTING VERTICES");
        }
    }

    /**
     * Adds a list of quads to be rendered
     *
     * @param quads list of quads
     */
    @Override
    public void addQuads(List<BakedQuad> quads) {
        if (drawMode != DRAW_MODE_NOT_DRAWING) {
            for (BakedQuad quad : quads) {
                final BakedQuad trans = transformQuads(quad);
                if (this.face.accepts(trans.getFace())) {
                    this.quads.add(trans);
                }
            }
        } else {
            throw new RuntimeException("NOT CONSTRUCTING VERTICES");
        }
    }

    /**
     * Adds a vertex
     *
     * @param x the x-coordinate for the vertex
     * @param y the y-coordinate for the vertex
     * @param z the z-coordinate for the vertex
     * @param sprite the icon
     * @param u u value for the vertex
     * @param v v value for the vertex
     */
    @Override
    public void addVertexWithUV(float x, float y, float z, TextureAtlasSprite sprite, float u, float v) {
        if (sprite == null) {
            sprite = this.getMissingSprite();
        }
        this.icon = sprite;
        this.addVertexWithUV(x, y, z, sprite.getInterpolatedU(u), sprite.getInterpolatedV(v));
    }

    /**
     * Adds a vertex
     *
     * @param x the x-coordinate for the vertex
     * @param y the y-coordinate for the vertex
     * @param z the z-coordinate for the vertex
     * @param u u value for the vertex
     * @param v v value for the vertex
     */
    @Override
    public void addVertexWithUV(float x, float y, float z, float u, float v) {
        if (this.drawMode == DRAW_MODE_NOT_DRAWING) {
            throw new RuntimeException("NOT CONSTRUCTING VERTICES");
        }
        
        // Create and transform the point.
        final Vector4f pos = new Vector4f(x, y, z, 1);
        this.transform(pos);
        
        // Create the new vertex data element.
        final VertexData vert = new VertexData(getVertexFormat());
        vert.setXYZ(pos.getX(), pos.getY(), pos.getZ());
        vert.setUV(u, v);
        vert.setRGBA(this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
        vert.setNormal(this.getNormal().getX(), this.getNormal().getY(), this.getNormal().getZ());
        this.vertexData.add(vert);
        
        if (this.vertexData.size() == this.drawMode) {
            final Direction dir = Direction.getFacingFromVector(this.getNormal().getX(), this.getNormal().getY(), this.getNormal().getZ());
            if (this.face.accepts(dir)) {
                BakedQuadBuilder builder = new BakedQuadBuilder();
                builder.setQuadTint(this.getTintIndex());
                builder.setApplyDiffuseLighting(this.getApplyDiffuseLighting());
                builder.setQuadOrientation(dir);
                builder.setTexture(this.icon);
                for (VertexData vertex : this.vertexData) {
                    vertex.applyVertexData(builder);
                }
                quads.add(builder.build());
            }
            vertexData.clear();
        }
    }

    @Override
    public void drawScaledFace(float minX, float minY, float maxX, float maxY, Direction face, TextureAtlasSprite icon, float offset) {
        if (this.face.accepts(face)) {
            super.drawScaledFace(minX, minY, maxX, maxY, face, icon, offset);
        }
    }

    @Override
    public TextureAtlasSprite getIcon(RenderMaterial source) {
        if (this.textureFunction == null || source == null) {
            return super.getIcon(source);
        } else {
            return this.textureFunction.apply(source);
        }
    }

    @Override
    protected void applyColorMultiplier(Direction side) {
    }

    public TessellatorBakedQuad setTextureFunction(Function<RenderMaterial, TextureAtlasSprite> function) {
        this.textureFunction = function;
        return this;
    }

    public TessellatorBakedQuad setCurrentFace(@Nullable Direction face) {
        return this.setCurrentFace(Face.fromDirection(face));
    }

    public TessellatorBakedQuad setCurrentFace(@Nonnull Face face) {
        this.face = face;
        return this;
    }

    public enum Face {
        NONE(false),
        GENERAL(true),
        DOWN(Direction.DOWN),
        UP(Direction.UP),
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        WEST(Direction.WEST),
        EAST(Direction.EAST);

        private final Predicate<Direction> test;

        Face(boolean general) {
            this(dir -> general);
        }

        Face(Direction direction) {
            this(dir -> dir == direction);
        }

        Face(Predicate<Direction> test) {
            this.test = test;
        }

        public boolean accepts(@Nullable Direction direction) {
            return this.test.test(direction);
        }

        @Nonnull
        public static Face fromDirection(@Nullable Direction direction) {
            if(direction == null) {
                return GENERAL;
            }
            return values()[direction.ordinal() + 2];
        }
    }
}
