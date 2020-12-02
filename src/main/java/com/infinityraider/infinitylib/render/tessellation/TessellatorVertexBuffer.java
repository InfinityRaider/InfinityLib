package com.infinityraider.infinitylib.render.tessellation;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class TessellatorVertexBuffer extends TessellatorAbstractBase {

    private static final Map<BufferBuilder, ThreadLocal<TessellatorVertexBuffer>> instances = new HashMap<>();

    private final Tessellator tessellator;
    private final BufferBuilder buffer;

    private TessellatorVertexBuffer(BufferBuilder buffer, Tessellator tessellator) {
        this.buffer = buffer;
        this.tessellator = tessellator;
    }

    public static TessellatorVertexBuffer getInstance() {
        return getInstance(Tessellator.getInstance());
    }

    public static TessellatorVertexBuffer getInstance(Tessellator tessellator) {
        return getInstance(tessellator.getBuffer(), tessellator);
    }

    public static TessellatorVertexBuffer getInstance(BufferBuilder buffer) {
        return getInstance(buffer, null);
    }

    private static TessellatorVertexBuffer getInstance(BufferBuilder buffer, Tessellator tessellator) {
        if (!instances.containsKey(buffer)) {
            instances.put(buffer, new ThreadLocal<>());
        }
        ThreadLocal<TessellatorVertexBuffer> threadLocal = instances.get(buffer);
        TessellatorVertexBuffer tess = threadLocal.get();
        if (tess == null) {
            tess = new TessellatorVertexBuffer(buffer, tessellator);
            threadLocal.set(tess);
        }
        return tess;
    }

    /**
     * @return VertexBuffer object which this is currently tessellating vertices
     * for
     */
    public BufferBuilder getVertexBuffer() {
        return buffer;
    }

    /**
     * Sub delegated method call of the startDrawingQuads() method to ensure
     * correct call chain
     */
    @Override
    protected void onStartDrawingQuadsCall() {
        buffer.begin(GL11.GL_QUADS, getVertexFormat());
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

    /**
     * Sub delegated method call of the draw() method to ensure correct call
     * chain.
     */
    @Override
    protected void onDrawCall() {
        if (tessellator != null) {
            tessellator.draw();
        } else {
            buffer.finishDrawing();
        }
    }

    /**
     * Adds a list of quads to be rendered
     *
     * @param quads list of quads
     */
    @Override
    public void addQuads(List<BakedQuad> quads) {
        this.transformQuads(quads).forEach(quad -> this.buffer.addQuad(
                this.getMatrixStackEntry(), quad, this.getRed(), this.getBlue(), this.getGreen(), this.getBrightness(), OverlayTexture.NO_OVERLAY));
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
        final Vector4f pos = new Vector4f(x, y, z, 1);
        this.transform(pos);
        List<VertexFormatElement> elements = this.getVertexFormat().getElements();
        if(elements.contains(DefaultVertexFormats.POSITION_3F)) {
            buffer.pos(pos.getX(), pos.getY(), pos.getZ());
        }
        if(elements.contains(DefaultVertexFormats.TEX_2F)) {
            buffer.tex(u, v);
        }
        if(elements.contains(DefaultVertexFormats.COLOR_4UB)) {
            buffer.color((int) (this.getRed() * 255), (int) (this.getGreen() * 255), (int) (this.getBlue() * 255), (int) (this.getAlpha() * 255));
        }
        if(elements.contains(DefaultVertexFormats.NORMAL_3B)) {
            buffer.normal(this.getNormal().getX(), this.getNormal().getY(), this.getNormal().getZ());
        }
        buffer.endVertex();
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
