package com.infinityraider.infinitylib.render.tessellation;

import com.github.quikmod.quikutil.exception.ContextedRuntimeException;
import com.github.quikmod.quikutil.exception.ExceptionContext;
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
import java.util.function.Function;

import org.joml.Vector4f;

/**
 * Helper class to construct vertices
 */
@SideOnly(Side.CLIENT)
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
     * The VertexCreator instance
     */
    private static final ThreadLocal<TessellatorBakedQuad> INSTANCE = ThreadLocal.withInitial(TessellatorBakedQuad::new);

    /* Getter for the VertexCreator instance */
    public static TessellatorBakedQuad getInstance() {
        return INSTANCE.get();
    }

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
    private EnumFacing face;
    /**
     * Icon currently drawing with
     */
    private TextureAtlasSprite icon;
    /**
     * Texture function
     */
    private Function<ResourceLocation, TextureAtlasSprite> textureFunction;

    /**
     * Private constructor
     */
    private TessellatorBakedQuad() {
        super();
        this.quads = new ArrayList<>();
        this.vertexData = new ArrayList<>();
        this.drawMode = DRAW_MODE_NOT_DRAWING;
    }

    @Override
    protected void addExtendedContextInformation(ExceptionContext context) {
        context.withEntry("Draw Mode", this.drawMode);
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
        // Validate renderer drawing mode.
        if (this.drawMode != DRAW_MODE_NOT_DRAWING) {
            // Create a new contexted exception.
            final ContextedRuntimeException e = new ContextedRuntimeException("NOT CONSTRUCTING VERTICES");
            
            // Add contextual information.
            this.addContextInformation(e.getContext());
            
            // Add the transition to the context.
            e.getContext().withEntry("New Draw Mode", mode);
            
            // Finally, throw the exception.
            throw e;
        }

        // Update the rendering mode.
        this.drawMode = mode;
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
        // Validate renderer drawing mode.
        if (this.drawMode == DRAW_MODE_NOT_DRAWING) {
            final ContextedRuntimeException e = new ContextedRuntimeException("NOT CONSTRUCTING VERTICES");
            this.addContextInformation(e.getContext());
            throw e;
        }

        // Finalize the draw.
        this.quads.clear();
        this.vertexData.clear();
        this.drawMode = DRAW_MODE_NOT_DRAWING;
        this.textureFunction = null;
        this.face = null;
    }

    /**
     * Adds a list of quads to be rendered
     *
     * @param quads list of quads
     */
    @Override
    public void addQuads(List<BakedQuad> quads) {
        // Validate renderer drawing mode.
        if (this.drawMode == DRAW_MODE_NOT_DRAWING) {
            final ContextedRuntimeException e = new ContextedRuntimeException("NOT CONSTRUCTING VERTICES");
            this.addContextInformation(e.getContext());
            throw e;
        }

        // Perform quad addition.
        for (BakedQuad quad : quads) {
            final BakedQuad trans = transformQuad(quad);
            if (trans.getFace() == this.face) {
                this.quads.add(trans);
            }
        }
    }

    /**
     * Adds a vertex
     *
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
        this.icon = icon;
        this.addVertexWithUV(x, y, z, icon.getInterpolatedU(u), icon.getInterpolatedV(v));
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
        // Validate renderer drawing mode.
        if (this.drawMode == DRAW_MODE_NOT_DRAWING) {
            final ContextedRuntimeException e = new ContextedRuntimeException("NOT CONSTRUCTING VERTICES");
            this.addContextInformation(e.getContext());
            throw e;
        }

        // Create and transform the point.
        final Vector4f pos = new Vector4f(x, y, z, 1);
        this.transform(pos);

        // Create the new vertex data element.
        final VertexData vert = new VertexData(getVertexFormat());
        vert.setXYZ(pos.x, pos.y, pos.z);
        vert.setUV(u, v);
        vert.setRGBA(r, g, b, a);
        vert.setNormal(normal.x, normal.y, normal.z);
        vertexData.add(vert);

        if (vertexData.size() == drawMode) {
            final EnumFacing dir = EnumFacing.getFacingFromVector(normal.x, normal.y, normal.z);
            if (dir == this.face) {
                UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder(getVertexFormat());
                quadBuilder.setQuadTint(getTintIndex());
                quadBuilder.setApplyDiffuseLighting(getApplyDiffuseLighting());
                quadBuilder.setQuadOrientation(dir);
                quadBuilder.setTexture(this.icon);
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
        if (this.face == face) {
            super.drawScaledFace(minX, minY, maxX, maxY, face, icon, offset);
        }
    }

    @Override
    public TextureAtlasSprite getIcon(ResourceLocation loc) {
        if (this.textureFunction == null || loc == null) {
            return super.getIcon(loc);
        } else {
            return this.textureFunction.apply(loc);
        }
    }

    @Override
    protected void applyColorMultiplier(EnumFacing side) {
    }

    public TessellatorBakedQuad setTextureFunction(Function<ResourceLocation, TextureAtlasSprite> function) {
        this.textureFunction = function;
        return this;
    }

    public TessellatorBakedQuad setCurrentFace(EnumFacing face) {
        this.face = face;
        return this;
    }
}
