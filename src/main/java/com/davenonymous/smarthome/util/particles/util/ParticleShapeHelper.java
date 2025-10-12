package com.davenonymous.smarthome.util.particles.util;

import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;

public class ParticleShapeHelper {
	private AABB modelBoundingBox;
	private Direction.Axis stackDirection;
	private double particleSpacing;

	public ParticleShapeHelper(AABB modelBoundingBox) {
		this(modelBoundingBox, determineStackDirection(modelBoundingBox));
	}

	public ParticleShapeHelper(AABB modelBoundingBox, Direction.Axis stackDirection) {
		this.modelBoundingBox = modelBoundingBox;
		this.stackDirection = stackDirection;
		this.particleSpacing = modelBoundingBox.max(stackDirection) / 16d;
	}

	private Axis getAxisDirection(double sX, double sY, double sZ, double eX, double eY, double eZ) {
		Axis edgeAxis = null;
		if(sX == eX && sY == eY) {
			if(sZ < eZ) {
				edgeAxis = Axis.ZP;
			} else if(sZ > eZ) {
				edgeAxis = Axis.ZN;
			}
		} else if(sX == eX && sZ == eZ) {
			if(sY < eY) {
				edgeAxis = Axis.YP;
			} else if(sY > eY) {
				edgeAxis = Axis.YN;
			}
		} else if(sY == eY && sZ == eZ) {
			if(sX < eX) {
				edgeAxis = Axis.XP;
			} else if(sX > eX) {
				edgeAxis = Axis.XN;
			}
		}

		return edgeAxis;
	}

	public Collection<ParticlePositionData> shape(VoxelShape shape) {
		BoxLineCache lines = new BoxLineCache();
		lines.addShape(shape);
		Set<ParticlePositionData> particlePositions = new HashSet<>();
		for(var line : lines.lines) {
			List<ParticlePositionData> lineParticles = line(line.start(), line.end());
			for(var p : lineParticles) {
				if(particlePositions.contains(p)) {
					particlePositions.remove(p);
				} else {
					particlePositions.add(p);
				}
			}
		}

		return particlePositions;
	}

	public List<ParticlePositionData> line(Vec3 start, Vec3 end) {
		Axis edgeAxis = getAxisDirection(start.x, start.y, start.z, end.x, end.y, end.z);
		if(edgeAxis == null) {
			return List.of();
		}

		double distance = start.distanceTo(end);
		int particleCount = (int)(distance / particleSpacing);
		Vec3 direction = end.subtract(start).normalize();
		List<ParticlePositionData> particles = new ArrayList<>();
		for(int i = 0; i < particleCount; i++) {
			Vec3 position = start.add(direction.scale(i * particleSpacing)).add(direction.scale(0.5));
			particles.add(new ParticlePositionData(position, edgeAxis));
		}
		return particles;
	}

	private static Direction.Axis determineStackDirection(AABB box) {
		double xLength = box.maxX - box.minX;
		double yLength = box.maxY - box.minY;
		double zLength = box.maxZ - box.minZ;

		if(xLength >= yLength && xLength >= zLength) {
			return Direction.Axis.X;
		} else if(yLength >= xLength && yLength >= zLength) {
			return Direction.Axis.Y;
		} else {
			return Direction.Axis.Z;
		}
	}

}
