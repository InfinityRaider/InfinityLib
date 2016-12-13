package com.infinityraider.infinitylib.render.tessellation;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to construct vertices
 */
@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class TessellatorBakedQuad extends TessellatorAbstractBase {
    /** Draw mode when no vertices are being constructed */
    public static final int DRAW_MODE_NOT_DRAWING = -1;
    /** Draw mode when vertices are being constructed for quads */
    public static final int DRAW_MODE_QUADS = 4;

    /** The VertexCreator instance */
    private static final ThreadLocal<TessellatorBakedQuad> INSTANCE = new ThreadLocal<>();

    /** Getter for the VertexCreator instance */
    public static TessellatorBakedQuad getInstance() {
        TessellatorBakedQuad tessellator = INSTANCE.get();
        if(tessellator == null) {
            tessellator = new TessellatorBakedQuad();
            INSTANCE.set(tessellator);
        }
        return tessellator;
    }

    /** Currently constructed quads */
    private final List<BakedQuad> quads;
    /** Currently constructed vertices */
    private final List<VertexData> vertexData;

    /** Current drawing mode */
    private int drawMode;
    /** Face being drawn */
    private EnumFacing face;
    /** Texture function */
    private Function<ResourceLocation, TextureAtlasSprite> textureFunction;

    /** Private constructor */
    private TessellatorBakedQuad() {
        super();
        this.quads = new ArrayList<>();
        this.vertexData = new ArrayList<>();
        this.drawMode = DRAW_MODE_NOT_DRAWING;
    }

    /**
     * Sub delegated method call of the startDrawingQuads() method to ensure correct call chain
     */
    @Override
    protected void onStartDrawingQuadsCall() {
        this.startDrawing(DRAW_MODE_QUADS);
    }

    /**
     * Method to start constructing vertices
     * @param mode draw mode
     */
    public void startDrawing(int mode) {
        if(drawMode == DRAW_MODE_NOT_DRAWING) {
            this.drawMode = mode;
        } else {
            throw new RuntimeException("ALREADY CONSTRUCTING VERTICES");
        }
    }

    /**
     * Method to get all quads constructed
     * @return list of quads, may be empty but never null
     */
    @Override
    public List<BakedQuad> getQuads() {
        return ImmutableList.copyOf(this.quads);
    }

    /**
     * Sub delegated method call of the draw() method to ensure correct call chain
     */
    @Override
    protected void onDrawCall() {
        if(drawMode != DRAW_MODE_NOT_DRAWING) {
            quads.clear();
            vertexData.clear();
            this.drawMode = DRAW_MODE_NOT_DRAWING;
            this.textureFunction = null;
            this.face = null;
        } else {
            throw new RuntimeException("NOT CONSTRUCTING VERTICES");
        }
    }

    /**
     * Adds a list of quads to be rendered
     * @param quads list of quads
     */
    @Override
    public void addQuads(List<BakedQuad> quads) {
        if(drawMode != DRAW_MODE_NOT_DRAWING) {
            List<BakedQuad> transFormedQuads = this.transformQuads(quads);
            for(BakedQuad quad : transFormedQuads) {
                if(quad.getFace() == this.face) {
                    this.quads.add(quad);
                }
            }
        } else {
            throw new RuntimeException("NOT CONSTRUCTING VERTICES");
        }
    }

    /**
     * Adds a vertex
     * @param x the x-coordinate for the vertex
     * @param y the y-coordinate for the vertex
     * @param z the z-coordinate for the vertex
     * @param u u value for the vertex
     * @param v v value for the vertex
     */
    @Override
    public void addVertexWithUV(float x, float y, float z, float u, float v) {
        this.addVertexAndQuad(x, y, z, u, v, null);
    }

    /**
     * Adds a vertex
     * @param x the x-coordinate for the vertex
     * @param y the y-coordinate for the vertex
     * @param z the z-coordinate for the vertex
     * @param icon the icon
     * @param u u value for the vertex
     * @param v v value for the vertex
     */
    @Override
    public void addVertexWithUV(float x, float y, float z, TextureAtlasSprite icon, float u, float v) {
        if (icon == null) {
            icon = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }
        this.addVertexAndQuad(x, y, z, icon.getInterpolatedU(u), icon.getInterpolatedV(v), icon);
    }

    protected void addVertexAndQuad(float x, float y, float z, float u, float v, TextureAtlasSprite quadTexture) {
        if (drawMode == DRAW_MODE_NOT_DRAWING) {
            throw new RuntimeException("NOT CONSTRUCTING VERTICES");
        }
        double[] coords = this.getTransformationMatrix().transform(x, y, z);
        vertexData.add(new VertexData(getVertexFormat(), (float) coords[0], (float) coords[1], (float) coords[2], u, v)
                .setRGBA(getRedValueFloat(), getGreenValueFloat(), getBlueValueFloat(), getAlphaValueFloat())
                .setNormal(getNormal().x, getNormal().y, getNormal().z));
        if (vertexData.size() == drawMode) {
            EnumFacing dir = EnumFacing.getFacingFromVector(getNormal().x, getNormal().y, getNormal().z);
            if (dir == this.face) {
                UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder(getVertexFormat());
                quadBuilder.setQuadTint(getTintIndex());
                quadBuilder.setApplyDiffuseLighting(getApplyDiffuseLighting());
                quadBuilder.setQuadOrientation(EnumFacing.getFacingFromVector(getNormal().x, getNormal().y, getNormal().z));
                quadBuilder.setTexture(quadTexture);
                for (VertexData vertex : vertexData) {
                    vertex.applyVertexData(quadBuilder);
                }
                quads.add(quadBuilder.build());
            }
            vertexData.clear();
        }
    }

    @Override
    public void drawScaledFace(float minX, float minY, float maxX, float maxY, EnumFacing face, TextureAtlasSprite icon, float offset) {
        if(this.face == face) {
            super.drawScaledFace(minX, minY, maxX, maxY, face, icon, offset);
        }
    }

    @Override
    public TextureAtlasSprite getIcon(ResourceLocation loc) {
        if(this.textureFunction == null || loc == null) {
            return super.getIcon(loc);
        } else {
            return this.textureFunction.apply(loc);
        }
    }

    @Override
    protected void applyColorMultiplier(EnumFacing side) {}

    public TessellatorBakedQuad setTextureFunction(Function<ResourceLocation, TextureAtlasSprite> function) {
        this.textureFunction = function;
        return this;
    }

    public TessellatorBakedQuad setCurrentFace(EnumFacing face) {
        this.face = face;
        return this;
    }
}
