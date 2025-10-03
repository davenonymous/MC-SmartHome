package com.davenonymous.smarthome.lib.gui;

import com.mojang.math.Axis;

public class Animations {

	public static Animation spin(boolean clockwise, float speed) {
		return (graphics, partialTicks) -> {
			float angle = (clockwise ? 1 : -1) * partialTicks * speed % 360.0f;
			graphics.pose().mulPose(Axis.ZP.rotationDegrees(angle));
		};
	}
}
