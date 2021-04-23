package com.infinityraider.infinitylib.render.tessellation;

import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public interface ITessellator extends Function<RenderMaterial, TextureAtlasSprite>, IRenderUtilities {

    /**
     * Method to start constructing quads
     * @return this
     */
    ITessellator startDrawingQuads();

    /**
     * Method to get all quads constructed
     *
     * @return list of quads, may be empty but never null
     */
    ImmutableList<BakedQuad> getQuads();

    /**
     * Method to finalize drawing.
     * @return this
     */
    ITessellator draw();

    /**
     * Gets the current vertex format the tessellator is drawing with
     *
     * @return the vertex format
     */
    VertexFormat getVertexFormat();

    /**
     * Adds a list of quads to be rendered
     *
     * @param quads list of quads
     * @return this
     */
    ITessellator addQuads(List<BakedQuad> quads);

    /**
     * Pushes the current transformation matrix onto the stack.
     * @return this
     */
    ITessellator pushMatrix();

    /**
     * Pops the last matrix from the stack.
     * @return this
     */
    ITessellator popMatrix();

    /**
     * Adds a vertex
     *
     * @param x the x-coordinate for the vertex
     * @param y the y-coordinate for the vertex
     * @param z the z-coordinate for the vertex
     * @param u u value for the vertex
     * @param v v value for the vertex
     * @return this
     */
    ITessellator addVertexWithUV(float x, float y, float z, float u, float v);

    /**
     * Adds a vertex
     *
     * @param x the x-coordinate for the vertex
     * @param y the y-coordinate for the vertex
     * @param z the z-coordinate for the vertex
     * @param icon the icon
     * @param u u value for the vertex
     * @param v v value for the vertex
     * @return this
     */
    ITessellator addVertexWithUV(float x, float y, float z, TextureAtlasSprite icon, float u, float v);

    /**
     * Adds a vertex scaled by 1/16th of a block
     *
     * @param x the x-coordinate for the vertex
     * @param y the y-coordinate for the vertex
     * @param z the z-coordinate for the vertex
     * @param u u value for the vertex
     * @param v v value for the vertex
     * @return this
     */
    ITessellator addScaledVertexWithUV(float x, float y, float z, float u, float v);

    /**
     * Adds a vertex scaled by 1/16th of a block
     *
     * @param x the x-coordinate for the vertex
     * @param y the y-coordinate for the vertex
     * @param z the z-coordinate for the vertex
     * @param icon the icon
     * @param u u value for the vertex
     * @param v v value for the vertex
     * @return this
     */
    ITessellator addScaledVertexWithUV(float x, float y, float z, TextureAtlasSprite icon, float u, float v);

    /**
     * Adds a quad for a scaled face, the face is defined by minimum and maximum
     * coordinates
     *
     * This method will define its UVs based on the coordinates
     *
     * @param minX minimum 2D x-coordinate of the face
     * @param minY minimum 2D y-coordinate of the face
     * @param maxX maximum 2D x-coordinate of the face
     * @param maxY maximum 2D y-coordinate of the face
     * @param face orientation of the face
     * @param offset offset of the face along its normal
     * @return this
     */
    ITessellator drawScaledFace(float minX, float minY, float maxX, float maxY, Direction face, float offset);

    /**
     * Adds a quad for a scaled face, the face is defined by minimum and maximum
     * coordinates
     *
     * This method has its UVs defined explicitly
     *
     * @param minX minimum 2D x-coordinate of the face
     * @param minY minimum 2D y-coordinate of the face
     * @param maxX maximum 2D x-coordinate of the face
     * @param maxY maximum 2D y-coordinate of the face
     * @param face orientation of the face
     * @param offset offset of the face along its normal
     * @return this
     */
    ITessellator drawScaledFace(float minX, float minY, float maxX, float maxY, Direction face, float offset,
                        float u1, float v1, float u2, float v2);

    /**
     * Adds a quad for a scaled face, the face is defined by minimum and maximum
     * coordinates
     *
     * This method will define its UVs based on the coordinates
     *
     * @param minX minimum 2D x-coordinate of the face
     * @param minY minimum 2D y-coordinate of the face
     * @param maxX maximum 2D x-coordinate of the face
     * @param maxY maximum 2D y-coordinate of the face
     * @param face orientation of the face
     * @param icon icon to render the face with
     * @param offset offset of the face along its normal
     * @return this
     */
    ITessellator drawScaledFace(float minX, float minY, float maxX, float maxY, Direction face, TextureAtlasSprite icon, float offset);

    /**
     * Adds a quad for a scaled face, the face is defined by minimum and maximum
     * coordinates
     *
     * This method has its UVs defined explicitly
     *
     * @param minX minimum 2D x-coordinate of the face
     * @param minY minimum 2D y-coordinate of the face
     * @param maxX maximum 2D x-coordinate of the face
     * @param maxY maximum 2D y-coordinate of the face
     * @param face orientation of the face
     * @param icon icon to render the face with
     * @param offset offset of the face along its normal
     * @param u1 minimum u value
     * @param v1 minimum v value
     * @param u2 maximum u value
     * @param v2 maximum v value
     * @return this
     */
    ITessellator drawScaledFace(float minX, float minY, float maxX, float maxY, Direction face, TextureAtlasSprite icon, float offset,
                        float u1, float v1, float u2, float v2);

    /**
     * Adds two quads for a scaled face, this face will have both sides drawn.
     * The face is defined by minimum and maximum coordinates
     *
     * This method will define its UVs based on the coordinates
     *
     * @param minX minimum 2D x-coordinate of the face
     * @param minY minimum 2D y-coordinate of the face
     * @param maxX maximum 2D x-coordinate of the face
     * @param maxY maximum 2D y-coordinate of the face
     * @param face orientation of the face
     * @param offset offset of the face along its normal
     * @return this
     */
    ITessellator drawScaledFaceDouble(float minX, float minY, float maxX, float maxY, Direction face, float offset);

    /**
     * Adds two quads for a scaled face, this face will have both sides drawn.
     * The face is defined by minimum and maximum coordinates
     *
     * This method has its UVs defined explicitly
     *
     * @param minX minimum 2D x-coordinate of the face
     * @param minY minimum 2D y-coordinate of the face
     * @param maxX maximum 2D x-coordinate of the face
     * @param maxY maximum 2D y-coordinate of the face
     * @param face orientation of the face
     * @param offset offset of the face along its normal
     * @param u1 minimum u value
     * @param v1 minimum v value
     * @param u2 maximum u value
     * @param v2 maximum v value
     * @return this
     */
    ITessellator drawScaledFaceDouble(float minX, float minY, float maxX, float maxY, Direction face, float offset,
                              float u1, float v1, float u2, float v2);

    /**
     * Adds two quads for a scaled face, this face will have both sides drawn.
     * The face is defined by minimum and maximum coordinates
     *
     * This method will define its UVs based on the coordinates
     *
     * @param minX minimum 2D x-coordinate of the face
     * @param minY minimum 2D y-coordinate of the face
     * @param maxX maximum 2D x-coordinate of the face
     * @param maxY maximum 2D y-coordinate of the face
     * @param face orientation of the face
     * @param icon icon to render the face with
     * @param offset offset of the face along its normal
     * @return this
     */
    ITessellator drawScaledFaceDouble(float minX, float minY, float maxX, float maxY, Direction face, TextureAtlasSprite icon, float offset);

    /**
     * Adds two quads for a scaled face, this face will have both sides drawn.
     * The face is defined by minimum and maximum coordinates
     *
     * This method has its UVs defined explicitly
     *
     * @param minX minimum 2D x-coordinate of the face
     * @param minY minimum 2D y-coordinate of the face
     * @param maxX maximum 2D x-coordinate of the face
     * @param maxY maximum 2D y-coordinate of the face
     * @param face orientation of the face
     * @param icon icon to render the face with
     * @param offset offset of the face along its normal
     * @param u1 minimum u value
     * @param v1 minimum v value
     * @param u2 maximum u value
     * @param v2 maximum v value
     * @return this
     */
    ITessellator drawScaledFaceDouble(float minX, float minY, float maxX, float maxY, Direction face, TextureAtlasSprite icon, float offset,
                              float u1, float v1, float u2, float v2);

    /**
     * Adds 6 quads for a scaled prism, the prism is defined by maximum and
     * minimum 3D coordinates
     *
     * @param minX minimum x-coordinate of the face
     * @param minY minimum y-coordinate of the face
     * @param minZ maximum z-coordinate of the face
     * @param maxX maximum x-coordinate of the face
     * @param maxY maximum y-coordinate of the face
     * @param maxZ maximum z-coordinate of the face
     * @return this
     */
    ITessellator drawScaledPrism(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

    /**
     * Adds 6 quads for a scaled prism, the prism is defined by maximum and
     * minimum 3D coordinates
     *
     * @param minX minimum x-coordinate of the face
     * @param minY minimum y-coordinate of the face
     * @param minZ maximum z-coordinate of the face
     * @param maxX maximum x-coordinate of the face
     * @param maxY maximum y-coordinate of the face
     * @param maxZ maximum z-coordinate of the face
     * @param icon icon to render the prism with
     * @return this
     */
    ITessellator drawScaledPrism(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon);

    /**
     * Adds a specified number of quads to approximate a cylinder along the y axis
     *
     * @param x the center x-coordinate
     * @param y the bottom of the cylinder
     * @param z the center z-coordinate
     * @param r the radius of the cylinder
     * @param h the height of the cylinder
     * @param vMax the maximum v-coordinate of the texture to use
     * @param quads the number of quads
     * @return this
     */
    ITessellator drawScaledCylinder(float x, float y, float z, float r, float h, float vMax, int quads);

    /**
     * Adds a specified number of quads to approximate a cylinder along the y axis
     *
     * @param x the center x-coordinate
     * @param y the bottom of the cylinder
     * @param z the center z-coordinate
     * @param r the radius of the cylinder
     * @param h the height of the cylinder
     * @param texture the texture to use
     * @param vMax the maximum v-coordinate of the texture to use
     * @param quads the number of quads
     * @return this
     */
    ITessellator drawScaledCylinder(float x, float y, float z, float r, float h, TextureAtlasSprite texture, float vMax, int quads);

    /**
     * Adds a specified number of quads to approximate the inside of a cylinder along the y axis
     *
     * @param x the center x-coordinate
     * @param y the bottom of the cylinder
     * @param z the center z-coordinate
     * @param r the radius of the cylinder
     * @param h the height of the cylinder
     * @param vMax the maximum v-coordinate of the texture to use
     * @param quads the number of quads
     * @return this
     */
    ITessellator drawScaledCylinderInside(float x, float y, float z, float r, float h, float vMax, int quads);

    /**
     * Adds a specified number of quads to approximate the inside of a cylinder along the y axis
     *
     * @param x the center x-coordinate
     * @param y the bottom of the cylinder
     * @param z the center z-coordinate
     * @param r the radius of the cylinder
     * @param h the height of the cylinder
     * @param texture the texture to use
     * @param vMax the maximum v-coordinate of the texture to use
     * @param quads the number of quads
     * @return this
     */
    ITessellator drawScaledCylinderInside(float x, float y, float z, float r, float h, TextureAtlasSprite texture, float vMax, int quads);

    /**
     * Adds a specified number of quads to approximate the outside of a cylinder along the y axis
     *
     * @param x the center x-coordinate
     * @param y the bottom of the cylinder
     * @param z the center z-coordinate
     * @param r the radius of the cylinder
     * @param h the height of the cylinder
     * @param vMax the maximum v-coordinate of the texture to use
     * @param quads the number of quads
     * @return this
     */
    ITessellator drawScaledCylinderOutside(float x, float y, float z, float r, float h, float vMax, int quads);

    /**
     * Adds a specified number of quads to approximate the outside of a cylinder along the y axis
     *
     * @param x the center x-coordinate
     * @param y the bottom of the cylinder
     * @param z the center z-coordinate
     * @param r the radius of the cylinder
     * @param h the height of the cylinder
     * @param texture the texture to use
     * @param vMax the maximum v-coordinate of the texture to use
     * @param quads the number of quads
     * @return this
     */
    ITessellator drawScaledCylinderOutside(float x, float y, float z, float r, float h, TextureAtlasSprite texture, float vMax, int quads);

    /**
     * Translates the matrix by a vector defined by a BlockPos
     *
     * @param pos the BlockPos
     * @return this
     */
    ITessellator translate(BlockPos pos);

    /**
     * Translates the matrix by a vector defined by 3 coordinates
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return this
     */
    ITessellator translate(float x, float y, float z);

    /**
     * Rotates the matrix by an angle around the given direction, rotation
     * center is the current origin
     *
     * @param angle angle to rotate by
     * @param x the x direction
     * @param y the y direction
     * @param z the z direction
     * @return this
     */
    ITessellator rotate(float angle, float x, float y, float z);

    /**
     * Scales along each axis with the corresponding factor
     *
     * @param x the x-axis scale factor
     * @param y the y-axis scale factor
     * @param z the z-axis scale factor
     * @return this
     */
    ITessellator scale(float x, float y, float z);

    /**
     * Applies a transformation
     *
     * @param matrix the transformation matrix
     * @return this
     */
    ITessellator applyTransformation(Matrix4f matrix);

    /**
     * Transforms a given point according to the currently active transformation
     * matrix.
     *
     * @param pos the point to be transformed.
     * @return this
     */
    ITessellator transform(Vector4f pos);

    /**
     * Gets a TextureAtlasSprite icon from a ResourceLocation
     *
     * @param source the ResourceLocation
     * @return the icon
     */
    TextureAtlasSprite getIcon(RenderMaterial source);

    /**
     * Sets the current face being drawn.
     * Will also set the appropriate normal if no specific normal is defined
     *
     * @param face the face
     * @return this
     */
    default ITessellator setFace(@Nullable Direction face) {
        return this.setFace(Face.fromDirection(face));
    }

    /**
     * Sets the current face being drawn.
     * Will also set the appropriate normal if no specific normal is defined
     *
     * @param face the face
     * @return this
     */
    ITessellator setFace(@Nonnull Face face);

    /**
     * @return The current face being drawn
     */
    Face getFace();

    /**
     * Sets the normal for the tessellator
     *
     * @param x the normal x direction
     * @param y the normal y direction
     * @param z the normal z direction
     * @return this
     */
    ITessellator setNormal(float x, float y, float z);

    /**
     * Sets the normal for the tessellator
     *
     * @param vec the normal vector
     * @return this
     */
    ITessellator setNormal(Vector3f vec);

    /**
     * Gets the current normal for the tessellator
     *
     * @return the normal vector
     */
    Vector3f getNormal();

    /**
     * Gets the current color value as an rgb int
     *
     * @return the color multiplier
     */
    int getColor();

    /**
     * Sets the current color value based on red, green and blue int values, all
     * arguments should be between 0 and 1
     *
     * @param color vector containing the rgb color values
     * @return this
     */
    ITessellator setColorRGB(Vector3f color);

    /**
     * Sets the current color value based on red, green and blue int values, all
     * arguments should be between 0 and 1
     *
     * @param red the rgb red value
     * @param green the rgb green value
     * @param blue the rgb blue value
     * @return this
     */
    ITessellator setColorRGB(float red, float green, float blue);

    /**
     * Sets the current color value based on red, green, blue and alpha values,
     * all arguments should be between 0 and 1
     *
     * @param red the rgb red value
     * @param green the rgb green value
     * @param blue the rgb blue value
     * @param alpha the rgb alpha value
     * @return this
     */
    ITessellator setColorRGBA(float red, float green, float blue, float alpha);

    /**
     * Sets the current color's alpha value.
     *
     * @param alpha the new alpha value to be used.
     * @return this
     */
    ITessellator setAlpha(float alpha);

    /**
     * @return current blue value as float, will be between 0 and 1
     */
    float getRed();

    /**
     * @return current green value as float, will be between 0 and 1
     */
    float getGreen();

    /**
     * @return current blue value as float, will be between 0 and 1
     */
    float getBlue();

    /**
     * @return current alpha value as float, will be between 0 and 1
     */
    float getAlpha();

    /**
     * Sets the brightness of the tessellator
     *
     * @param value the brightness value
     * @return this
     */
    ITessellator setBrightness(int value);

    /**
     * Gets the brightness of the tessellator
     *
     * @return the brightness value
     */
    int getBrightness();


    /**
     * Sets the overlay of the tessellator
     *
     * @param value the overlay value
     * @return this
     */
    ITessellator setOverlay(int value);

    /**
     * Gets the overlay of the tessellator
     *
     * @return the overlay value
     */
    int getOverlay();
    /**
     * Sets the tint index value to use for the quads
     *
     * @param index the tint index
     * @return this
     */
    ITessellator setTintIndex(int index);

    /**
     * Gets the current tint index value to use for the quads
     *
     * @return the tint index
     */
    int getTintIndex();

    /**
     * Sets if diffuse lighting should be applied to the quads
     *
     * @param value the diffuse lighting setting
     * @return this
     */
    ITessellator setApplyDiffuseLighting(boolean value);

    /**
     * Gets if diffuse lighting is applied to the quads
     *
     * @return the diffuse lighting setting
     */
    boolean getApplyDiffuseLighting();

    @Override
    default TextureAtlasSprite apply(RenderMaterial source) {
        return this.getIcon(source);
    }

    class Defaults {
        /** Default color (white) */
        public static final float COLOR = 1.0F;

        /** Default brightness (max) */
        public static final int BRIGHTNESS = 15 << 24;

        /** Default overlay (none) */
        public static final int OVERLAY = OverlayTexture.NO_OVERLAY;

        /** Default normal (up) */
        public static final Vector3f NORMAL = new Vector3f(0, 1, 0);

        private Defaults() {}
    }

    /**
     * Enum to indicate faces being tessellated,
     * this differs with Vanilla's Direction enum as there is no ambiguity between none and general for null.
     * Also, no NullPointerExceptions
     */
    enum Face {
        NONE(false),
        GENERAL(true),
        DOWN(Direction.DOWN),
        UP(Direction.UP),
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        WEST(Direction.WEST),
        EAST(Direction.EAST);

        private final Predicate<Direction> test;
        private final Vector3f normal;

        Face(boolean general) {
            this(dir -> general, Defaults.NORMAL);
        }

        Face(Direction direction) {
            this(dir -> dir == direction, direction.toVector3f());
        }

        Face(Predicate<Direction> test, Vector3f normal) {
            this.test = test;
            this.normal = normal;
        }

        public boolean accepts(@Nullable Direction direction) {
            return this.test.test(direction);
        }

        public Vector3f getNormal() {
            return this.normal;
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
