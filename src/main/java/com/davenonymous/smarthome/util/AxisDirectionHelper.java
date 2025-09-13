package com.davenonymous.smarthome.util;

import com.mojang.math.Axis;
import net.minecraft.core.Direction;

public class AxisDirectionHelper {
	public static Axis axisFromByte(byte b) {
		return switch(b) {
			case 0 -> Axis.XN;
			case 1 -> Axis.XP;
			case 2 -> Axis.YN;
			case 3 -> Axis.YP;
			case 4 -> Axis.ZN;
			case 5 -> Axis.ZP;
			default -> Axis.XN;
		};
	}

	public static Direction directionFromByte(byte b) {
		return switch(b) {
			case 0 -> Direction.WEST;
			case 1 -> Direction.EAST;
			case 2 -> Direction.DOWN;
			case 3 -> Direction.UP;
			case 4 -> Direction.NORTH;
			case 5 -> Direction.SOUTH;
			default -> Direction.WEST;
		};
	}

	public static Axis axisFromDirection(Direction dir) {
		return switch(dir) {
			case EAST -> Axis.XP;
			case WEST -> Axis.XN;
			case UP -> Axis.YP;
			case DOWN -> Axis.YN;
			case SOUTH -> Axis.ZP;
			case NORTH -> Axis.ZN;
		};
	}

	public static Direction directionFromAxis(Axis axis) {
		return directionFromByte(axisToByte(axis));
	}

	public static byte axisToByte(Axis axis) {
		if(axis == Axis.XN) return (byte) 0;
		if(axis == Axis.XP) return (byte) 1;
		if(axis == Axis.YN) return (byte) 2;
		if(axis == Axis.YP) return (byte) 3;
		if(axis == Axis.ZN) return (byte) 4;
		if(axis == Axis.ZP) return (byte) 5;
		return (byte) 0;
	}
}
