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

@OnlyIn(Dist.CLIENT)
public class InfFaceBakery extends FaceBakery {
    private static final InfFaceBakery INSTANCE = new InfFaceBakery();

    public static InfFaceBakery getInstance() {
        return INSTANCE;
    }

    private QuadTransformer transformer;
    private TransformationMatrix matrix;
    private boolean initialized;

    private InfFaceBakery() {}

    public void init() {
        this.initialized = this.initialized || this.hijackVanillaFaceBakery();
    }

    public void startTransformingQuads(TransformationMatrix transform) {
        if(this.initialized) {
            this.matrix = transform;
            this.transformer = new QuadTransformer(transform);
        } else {
            InfinityLib.instance.getLogger().error("Can not transform quads, Face Bakery not successfully initialized");
        }
    }

    public void stopTransformingQuads() {
        this.matrix = null;
        this.transformer = null;
    }

    @Override
    public BakedQuad bakeQuad(Vector3f p1, Vector3f p2, BlockPartFace face, TextureAtlasSprite sprite, Direction facing,
                              IModelTransform transform, @Nullable BlockPartRotation partRotation, boolean shade, ResourceLocation location) {
        BakedQuad quad = super.bakeQuad(p1, p2, face, sprite, facing, transform, partRotation, shade, location);
        if(this.transformer == null || (this.matrix != null && this.matrix.isIdentity())) {
            return quad;
        } else {
            return this.transformer.processOne(quad);
        }
    }

    @Override
    public void rotateVertex(Vector3f posIn, TransformationMatrix transformIn) {
        super.rotateVertex(posIn, transformIn);
    }

    private boolean hijackVanillaFaceBakery() {
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
                        // Set field
                        field.set(null, getInstance());
                        InfinityLib.instance.getLogger().info("Hijacking successful, new destination: Blockhamas");
                        return true;
                    } catch(Exception e) {
                        InfinityLib.instance.getLogger().info("Hijacking failed, one ticket to prison obtained");
                        e.printStackTrace();
                        return false;
                    }
                })
                .orElseGet(() -> {
                    InfinityLib.instance.getLogger().info("Missed the plane, try catching the next flight");
                    return false;
                });
    }
}
