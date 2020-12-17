package com.infinityraider.infinitylib.render.model;

import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.QuadTransformer;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Face Bakery class which replaces the Vanilla Face Bakery, used to transform Vanilla Quads right after baking,
 * for instance in case of composite models.
 *
 * Caches and calls the previous version of the Face Bakery in case someone else had also injected themselves there as well.
 */
@OnlyIn(Dist.CLIENT)
public class TransformingFaceBakery extends FaceBakery {
    private static final TransformingFaceBakery INSTANCE = hijackVanillaFaceBakery();

    public static TransformingFaceBakery getInstance() {
        return INSTANCE;
    }

    // Just here, so we can call this method to make sure this class gets loaded in time
    public static void init() {}


    // Previous object in Vanilla's FaceBakery seat, calls are forwarded to this, so we don't break someone else's shit
    // in case they else over before us (hijackers together, strong)
    private final FaceBakery pilot;

    // Thread-safety first
    private final ThreadLocal<TransformStack> transforms;

    private TransformingFaceBakery(FaceBakery pilot) {
        this.pilot = pilot;
        this.transforms = ThreadLocal.withInitial(TransformStack::new);
    }

    public void pushQuadTransform(TransformationMatrix matrix) {
        this.transforms.get().push(matrix);
    }

    public void popQuadTransform() {
        this.transforms.get().pop();
    }

    @Override
    public BakedQuad bakeQuad(Vector3f p1, Vector3f p2, BlockPartFace face, TextureAtlasSprite sprite, Direction facing,
                              IModelTransform transform, @Nullable BlockPartRotation partRotation, boolean shade, ResourceLocation location) {
        return this.transforms.get().transform(this.pilot.bakeQuad(p1, p2, face, sprite, facing, transform, partRotation, shade, location));
    }

    @Override
    public void rotateVertex(Vector3f posIn, TransformationMatrix transformIn) {
        this.pilot.rotateVertex(posIn, transformIn);
    }

    // Stack approach to correctly handle nesting of composite models
    private static class TransformStack {
        private Layer layer;

        private TransformStack() {
            this.layer = BASE_LAYER;
        }

        public TransformStack push(TransformationMatrix matrix) {
            this.layer = new Layer(this.layer, matrix);
            return this;
        }

        public TransformStack pop() {
            this.layer = this.layer.getPreviousLayer();
            return this;
        }

        public BakedQuad transform(BakedQuad quad) {
            return this.layer.transform(quad);
        }

        private static final Layer BASE_LAYER = new Layer(null, TransformationMatrix.identity()) {
            @Override
            public TransformationMatrix getCurrentMatrix() {
                //override this method to prevent NPE in the constructor
                return TransformationMatrix.identity();
            }

            @Override
            protected Layer getPreviousLayer() {
                //this is the base layer, there is no previous layer
                return this;
            }
        };

        private static class Layer {
            private final Layer previous;

            private final TransformationMatrix currentMatrix;
            private final QuadTransformer currentTransformer;

            private Layer(Layer previous, TransformationMatrix matrix) {
                this.previous = previous;
                this.currentMatrix = this.getPreviousLayer().getCurrentMatrix().compose(matrix);
                // no need to create a new object if the transformation matrix is the identity matrix
                this.currentTransformer = this.getCurrentMatrix().isIdentity() ? null : new QuadTransformer(this.getCurrentMatrix());
            }

            protected Layer getPreviousLayer() {
                return this.previous;
            }

            public TransformationMatrix getCurrentMatrix() {
                return this.currentMatrix;
            }

            public BakedQuad transform(BakedQuad quad) {
                // quad transformer is null in case of identity transformation matrix
                return this.currentTransformer == null ? quad : this.currentTransformer.processOne(quad);
            }
        }
    }

    // Method to inject ourselves into Vanilla's FaceBakery, returns a dummy in case injection fails
    private static TransformingFaceBakery hijackVanillaFaceBakery() {
        InfinityLib.instance.getLogger().info("Trying to hijack Vanilla Face Bakery");
        return Arrays.stream(BlockModel.class.getDeclaredFields())
                .filter(field -> field.getType().isAssignableFrom(FaceBakery.class))
                .findAny()
                .map(field -> {
                    try {
                        // Set accessible
                        field.setAccessible(true);
                        // Remove final modifier
                        Field modifiers = Field.class.getDeclaredField("modifiers");
                        modifiers.setAccessible(true);
                        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                        // Fetch previous value
                        FaceBakery captain = (FaceBakery) field.get(null);
                        // Set field
                        TransformingFaceBakery lookAtMeImTheCaptainNow = new TransformingFaceBakery(captain);
                        field.set(null, lookAtMeImTheCaptainNow);
                        InfinityLib.instance.getLogger().info("Hijacking successful, new destination: Blockhamas");
                        return lookAtMeImTheCaptainNow;
                    } catch(Exception e) {
                        // this might happen
                        InfinityLib.instance.getLogger().info("Hijacking failed, one ticket to prison obtained");
                        e.printStackTrace();
                        return DUMMY;
                    }
                })
                .orElseGet(() -> {
                    // this should never happen but still has to be here to make the code compile
                    InfinityLib.instance.getLogger().info("Missed the plane, try catching the next flight");
                    return DUMMY;
                });
    }

    // Dummy object in case hijacking goes wrong, this will forward baking calls to the vanilla logic if called directly,
    // and log errors when trying to push transformations
    private static final TransformingFaceBakery DUMMY = new TransformingFaceBakery(null) {
        @Override
        public void pushQuadTransform(TransformationMatrix matrix) {
            InfinityLib.instance.getLogger().error("Can not apply transform, Face Bakery has not been successfully hijacked");
        }

        public void popQuadTransform() {}

        @Override
        public BakedQuad bakeQuad(Vector3f p1, Vector3f p2, BlockPartFace face, TextureAtlasSprite sprite, Direction facing,
                                  IModelTransform transform, @Nullable BlockPartRotation partRotation, boolean shade, ResourceLocation location) {
            return super.bakeQuad(p1, p2, face, sprite, facing, transform, partRotation, shade, location);
        }

        @Override
        public void rotateVertex(Vector3f posIn, TransformationMatrix transformIn) {
            super.rotateVertex(posIn, transformIn);
        }
    };
}
