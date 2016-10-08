package com.infinityraider.infinitylib.render.tessellation;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class TessellatorVertexBuffer extends TessellatorAbstractBase {
    private static final Map<VertexBuffer, TessellatorVertexBuffer> instances = new HashMap<>();

    private final Tessellator tessellator;
    private final VertexBuffer buffer;
    private final Set<VertexFormatElement.EnumUsage> vertexFormatUsage;

    private TessellatorVertexBuffer(VertexBuffer buffer, Tessellator tessellator) {
        this.buffer = buffer;
        this.tessellator = tessellator;
        this.vertexFormatUsage = new HashSet<>();
    }

    public static TessellatorVertexBuffer getInstance() {
        return getInstance(Tessellator.getInstance());
    }

    public static TessellatorVertexBuffer getInstance(Tessellator tessellator) {
        final VertexBuffer buffer = tessellator.getBuffer();
        if (instances.containsKey(buffer)) {
            return instances.get(buffer).reset();
        } else {
            final TessellatorVertexBuffer tess = new TessellatorVertexBuffer(buffer, tessellator);
            instances.put(buffer, tess);
            return tess;
        }
    }

    public static TessellatorVertexBuffer getInstance(VertexBuffer buffer) {
        if (instances.containsKey(buffer)) {
            return instances.get(buffer).reset();
        } else {
            final TessellatorVertexBuffer tess = new TessellatorVertexBuffer(buffer, null);
            instances.put(buffer, tess);
            return tess;
        }
    }

    /**
     * @return VertexBuffer object which this is currently tessellating vertices for
     */
    public VertexBuffer getVertexBuffer() {
        return buffer;
    }

    /**
     * Sub delegated method call of the startDrawingQuads() method to ensure correct call chain
     */
    @Override
    protected void onStartDrawingQuadsCall() {
        this.vertexFormatUsage.addAll(this.getVertexFormat().getElements().stream().map(VertexFormatElement::getUsage).collect(Collectors.toList()));
        buffer.begin(GL11.GL_QUADS, this.getVertexFormat());
    }
    /**
     * Method to get all quads constructed
     * @return emtpy list, no quads are constructed here
     */
    @Override
    public List<BakedQuad> getQuads() {
        return ImmutableList.of();
    }

    /**
     * Sub delegated method call of the draw() method to ensure correct call chain
     */
    @Override
    protected void onDrawCall() {
        this.vertexFormatUsage.clear();
        if (tessellator != null) {
            tessellator.draw();
        } else {
            buffer.finishDrawing();
        }
    }

    /**
     * Adds a list of quads to be rendered
     * @param quads list of quads
     */
    @Override
    public void addQuads(List<BakedQuad> quads) {
        for(BakedQuad quad : this.transformQuads(quads)) {
            buffer.addVertexData(quad.getVertexData());
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
        double[] coords = this.getTransformationMatrix().transform(x, y, z);
        if(this.vertexFormatUsage.contains(VertexFormatElement.EnumUsage.POSITION)) {
            buffer.pos(coords[0], coords[1], coords[2]);
        }
        if(this.vertexFormatUsage.contains(VertexFormatElement.EnumUsage.COLOR)) {
            buffer.color(getRedValueInt(), getGreenValueInt(), getBlueValueInt(), getAlphaValueInt());
        }
        if(this.vertexFormatUsage.contains(VertexFormatElement.EnumUsage.UV)) {
            buffer.tex(u, v);
        }
        buffer.lightmap(getBrightness()>> 16 & 65535, getBrightness() & 65535);
        if(this.vertexFormatUsage.contains(VertexFormatElement.EnumUsage.NORMAL)) {
            buffer.normal(getNormal().x, getNormal().y, getNormal().z);
        }
        buffer.endVertex();
    }

    /**
     * Resets the tessellator
     * @return this
     */
    private TessellatorVertexBuffer reset() {
        this.resetMatrix();
        this.setColorRGBA(STANDARD_COLOR, STANDARD_COLOR, STANDARD_COLOR, STANDARD_COLOR);
        this.setBrightness(STANDARD_BRIGHTNESS);
        return this;
    }

    @Override
    protected void applyColorMultiplier(EnumFacing side) {
        float preMultiplier = getMultiplier(transformSide(side));
        float r = preMultiplier * ((float) (this.getRedValueInt()))/255.0F;
        float g = preMultiplier * ((float) (this.getGreenValueInt()))/255.0F;
        float b = preMultiplier * ((float) (this.getBlueValueInt()))/255.0F;
        this.setColorRGBA_F(r, g, b, this.getAlphaValueFloat());
    }

    private EnumFacing transformSide(EnumFacing dir) {
        if(dir == null) {
            return null;
        }
        double[] coords = this.getTransformationMatrix().transform(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ());
        double[] translation = this.getTransformationMatrix().getTranslation();
        coords[0] = coords[0] - translation[0];
        coords[1] = coords[1] - translation[1];
        coords[2] = coords[2] - translation[2];
        double x = Math.abs(coords[0]);
        double y = Math.abs(coords[1]);
        double z = Math.abs(coords[2]);
        if(x > z) {
            if(x > y) {
                return coords[0] > 0 ? EnumFacing.EAST : EnumFacing.WEST;
            }
        } else {
            if(z > y) {
                return coords[2] > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
            }
        }
        return coords[1] > 0 ? EnumFacing.UP : EnumFacing.DOWN;
    }

    private float getMultiplier(EnumFacing side) {
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
