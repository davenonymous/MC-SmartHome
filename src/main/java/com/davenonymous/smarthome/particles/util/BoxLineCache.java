package com.davenonymous.smarthome.particles.util;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashSet;
import java.util.Set;

public class BoxLineCache {

	public Set<Line> lines = new HashSet<>();

	public BoxLineCache() {

	}

	public void clear() {
		this.lines.clear();
	}

	public void addShape(VoxelShape shape) {
		shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
			var newLine = new Line(
				new Vec3(x1, y1, z1),
				new Vec3(x2, y2, z2)
			);
			if(lines.contains(newLine)) {
				lines.remove(newLine);
			} else {
				lines.add(newLine);
			}
		});
	}

	public record Line(Vec3 start, Vec3 end) {
		public Line(Vec3 start, Vec3 end) {
			Vec3 origin = new Vec3(0,0,0);
			if(start.distanceTo(origin) < end.distanceTo(origin)) {
				this.start = start;
				this.end = end;
			} else {
				this.end = start;
				this.start = end;
			}
		}
	}

}
