package com.infinityraider.infinitylib.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.ItemTransforms;

import java.util.function.Function;

/**
 * Borrowed from MinecraftForge base code.
 *
 * Had to create a new class since this is hidden in a jumble of forge code normally.
 *
 * @author Forge Team
 */
@SuppressWarnings("unused")
public class DefaultTransforms {
    // Must go first, for proper classloading.
    private static final Transformation flipX = new Transformation(null, null, new Vector3f(-1, 1, 1), null);

    public static final ImmutableMap<ItemTransforms.TransformType, Transformation> BLOCK = generateBlockTransform();
    public static final ImmutableMap<ItemTransforms.TransformType, Transformation> ITEM = generateItemTransform();

    private static ImmutableMap<ItemTransforms.TransformType, Transformation> generateBlockTransform() {
        final Transformation thirdperson = get(0, 2.5f, 0, 75, 45, 0, 0.375f);
        final ImmutableMap.Builder<ItemTransforms.TransformType, Transformation> transform = new ImmutableMap.Builder<>();
        transform.put(ItemTransforms.TransformType.GUI, get(0, 0, 0, 30, 225, 0, 0.625f));
        transform.put(ItemTransforms.TransformType.GROUND, get(0, 3, 0, 0, 0, 0, 0.25f));
        transform.put(ItemTransforms.TransformType.FIXED, get(0, 0, 0, 0, 0, 0, 0.5f));
        transform.put(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
        transform.put(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
        transform.put(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 45, 0, 0.4f));
        transform.put(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, get(0, 0, 0, 0, 225, 0, 0.4f));
        return transform.build();
    }

    private static ImmutableMap<ItemTransforms.TransformType, Transformation> generateItemTransform() {
        final Transformation thirdperson = get(0, 3, 1, 0, 0, 0, 0.55f);
        final Transformation firstperson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
        final ImmutableMap.Builder<ItemTransforms.TransformType, Transformation> transform = new ImmutableMap.Builder<>();
        transform.put(ItemTransforms.TransformType.GROUND, get(0, 2, 0, 0, 0, 0, 0.5f));
        transform.put(ItemTransforms.TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
        transform.put(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
        transform.put(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
        transform.put(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
        transform.put(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstperson));
        return transform.build();
    }

    private static Transformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new Transformation(
                new Vector3f(tx / 16, ty / 16, tz / 16),
                new Quaternion(ax, ay, az, true),
                new Vector3f(s, s, s),
                null
        );
    }

    private static Transformation leftify(Transformation transform) {
        return flipX.compose(transform.blockCornerToCenter()).compose(flipX).blockCenterToCorner();
    }

    public static final Matrix4f getBlockMatrix(ItemTransforms.TransformType type) {
        if (BLOCK.containsKey(type)) {
            return BLOCK.get(type).getMatrix();
        }  else {
            return Transformation.identity().getMatrix();
        }
    }

    public static final Matrix4f getItemMatrix(ItemTransforms.TransformType type) {
        if (ITEM.containsKey(type)) {
            return ITEM.get(type).getMatrix();
        } else {
            return Transformation.identity().getMatrix();
        }
    }
	
	public interface Transformer extends Function<ItemTransforms.TransformType, Matrix4f> {}
	
}
