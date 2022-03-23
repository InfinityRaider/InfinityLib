package com.infinityraider.infinitylib.utility;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AreaHelper {
	
	public static AABB getArea(Vec3 center, double radius) {
		return getArea(center, radius, radius, radius);
	}
	
	public static AABB getArea(Vec3 center, double dx, double dy, double dz) {
		return new AABB(
				center.x - dx,
				center.y - dy,
				center.z - dz,
				center.x + dx,
				center.y + dy,
				center.z + dz
		);
	}

	public static AABB getArea(Entity center, double radius) {
		return getArea(center, radius, radius, radius);
	}

	public static AABB getArea(Entity center, double dx, double dy, double dz) {
		return getArea(center.position(), dx, dy, dz);
	}
	
}
