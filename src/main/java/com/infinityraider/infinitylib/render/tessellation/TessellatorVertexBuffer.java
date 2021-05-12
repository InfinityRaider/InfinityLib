package com.infinityraider.infinitylib.render.tessellation;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class TessellatorVertexBuffer extends TessellatorAbstractBase {
    private final IRenderTypeBuffer.Impl buffer;
    private final RenderType renderType;

    private IVertexBuilder builder;

    public TessellatorVertexBuffer(IRenderTypeBuffer.Impl buffer, RenderType renderType) {
        this.buffer = buffer;
        this.renderType = renderType;
    }

    /**
     * @return IRenderTypeBuffer.Impl object which this is currently tessellating vertices for
     */
    public IRenderTypeBuffer.Impl getVertexBuffer() {
        return this.buffer;
    }

    /**
     * @return The RenderType this tessellator is currently drawing with
     */
    public RenderType getRenderType() {
        return this.renderType;
    }

    /**
     * Sub delegated method call of the startDrawingQuads() method to ensure
     * correct call chain
     */
    @Override
    protected void onStartDrawingQuadsCall() {
        this.builder = this.getVertexBuffer().getBuffer(this.getRenderType());
    }

    /**
     * Method to get all quads constructed.
     *
     * @return emtpy list, no quads are constructed here
     */
    @Override
    public ImmutableList<BakedQuad> getQuads() {
        return ImmutableList.of();
    }

    @Override
    public VertexFormat getVertexFormat() {
        return this.getRenderType().getVertexFormat();
    }

    /**
     * Sub delegated method call of the draw() method to ensure correct call
     * chain.
     */
    @Override
    protected void onDrawCall() {
        if (this.builder != null) {
            this.getVertexBuffer().finish(this.getRenderType());
            this.builder = null;
        }
    }

    /**
     * Adds a list of quads to be rendered
     *
     * @param quads list of quads
     */
    @Override
    public TessellatorVertexBuffer addQuads(List<BakedQuad> quads) {
        quads.forEach(quad -> this.builder.addQuad(
                this.getMatrixStackEntry(), quad, this.getRed(), this.getBlue(), this.getGreen(), this.getBrightness(), OverlayTexture.NO_OVERLAY));
        return this;
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
    public TessellatorVertexBuffer addVertexWithUV(float x, float y, float z, float u, float v) {
        final Vector4f pos = new Vector4f(x, y, z, 1);
        this.transform(pos);
        List<VertexFormatElement> elements = this.getVertexFormat().getElements();
        // Note: the order this vertex data is defined is important
        if(elements.contains(DefaultVertexFormats.POSITION_3F)) {
            builder.pos(pos.getX(), pos.getY(), pos.getZ());
        }
        if(elements.contains(DefaultVertexFormats.COLOR_4UB)) {
            builder.color((int) (this.getRed() * 255), (int) (this.getGreen() * 255), (int) (this.getBlue() * 255), (int) (this.getAlpha() * 255));
        }
        if(elements.contains(DefaultVertexFormats.TEX_2F)) {
            builder.tex(u, v);
        }
        if(elements.contains(DefaultVertexFormats.TEX_2S)) {
            builder.overlay(this.getOverlay());
        }
        if(elements.contains(DefaultVertexFormats.TEX_2SB)) {
            builder.lightmap(this.getBrightness());
        }
        if(elements.contains(DefaultVertexFormats.NORMAL_3B)) {
            builder.normal(this.getNormal().getX(), this.getNormal().getY(), this.getNormal().getZ());
        }
        builder.endVertex();
        return this;
    }

    @Override
    protected void applyColorMultiplier(Direction side) {
        // I gave up on transforming the side. This probably is faster anyway...
        final float preMultiplier = getMultiplier(side);
        this.setColorRGB(preMultiplier*this.getRed(), preMultiplier*this.getGreen(), preMultiplier*this.getBlue());
    }

    private float getMultiplier(Direction side) {
        switch (side) {
            case DOWN:
                return 0.5F;
            case NORTH:
            case SOUTH:
                return 0.8F;
            case EAST:
            case WEST:
                return 0.6F;
            default:
                return 1;
        }
    }
}
