package com.infinityraider.infinitylib.render;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;

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
    private static final TransformationMatrix flipX = new TransformationMatrix(null, null, new Vector3f(-1, 1, 1), null);

    public static final ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> BLOCK = generateBlockTransform();
    public static final ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> ITEM = generateItemTransform();

    private static ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> generateBlockTransform() {
        final TransformationMatrix thirdperson = get(0, 2.5f, 0, 75, 45, 0, 0.375f);
        final ImmutableMap.Builder<ItemCameraTransforms.TransformType, TransformationMatrix> transform = new ImmutableMap.Builder<>();
        transform.put(ItemCameraTransforms.TransformType.GUI, get(0, 0, 0, 30, 225, 0, 0.625f));
        transform.put(ItemCameraTransforms.TransformType.GROUND, get(0, 3, 0, 0, 0, 0, 0.25f));
        transform.put(ItemCameraTransforms.TransformType.FIXED, get(0, 0, 0, 0, 0, 0, 0.5f));
        transform.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
        transform.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
        transform.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 45, 0, 0.4f));
        transform.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, get(0, 0, 0, 0, 225, 0, 0.4f));
        return transform.build();
    }

    private static ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> generateItemTransform() {
        final TransformationMatrix thirdperson = get(0, 3, 1, 0, 0, 0, 0.55f);
        final TransformationMatrix firstperson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
        final ImmutableMap.Builder<ItemCameraTransforms.TransformType, TransformationMatrix> transform = new ImmutableMap.Builder<>();
        transform.put(ItemCameraTransforms.TransformType.GROUND, get(0, 2, 0, 0, 0, 0, 0.5f));
        transform.put(ItemCameraTransforms.TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
        transform.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
        transform.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
        transform.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
        transform.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstperson));
        return transform.build();
    }

    private static TransformationMatrix get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TransformationMatrix(
                new Vector3f(tx / 16, ty / 16, tz / 16),
                new Quaternion(ax, ay, az, true),
                new Vector3f(s, s, s),
                null
        );
    }

    private static TransformationMatrix leftify(TransformationMatrix transform) {
        return flipX.compose(transform.blockCornerToCenter()).compose(flipX).blockCenterToCorner();
    }

    public static final Matrix4f getBlockMatrix(ItemCameraTransforms.TransformType type) {
        if (BLOCK.containsKey(type)) {
            return BLOCK.get(type).getMatrix();
        }  else {
            return TransformationMatrix.identity().getMatrix();
        }
    }

    public static final Matrix4f getItemMatrix(ItemCameraTransforms.TransformType type) {
        if (ITEM.containsKey(type)) {
            return ITEM.get(type).getMatrix();
        } else {
            return TransformationMatrix.identity().getMatrix();
        }
    }
	
	public interface Transformer extends Function<ItemCameraTransforms.TransformType, Matrix4f> {}
	
}
