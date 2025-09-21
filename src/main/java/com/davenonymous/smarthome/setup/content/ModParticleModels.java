package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.phys.AABB;

public class ModParticleModels {
	public static final ModelResourceLocation BLOCK_MARKER_LINE = ModelResourceLocation.standalone(
		SmartHome.resource("block/block_marker_line")
	);
	public static final ModelResourceLocation CORNER_MARKER = ModelResourceLocation.standalone(
		SmartHome.resource("block/corner_marker")
	);
	public static final ModelResourceLocation CORNER_MARKER_ORANGE = ModelResourceLocation.standalone(
		SmartHome.resource("block/corner_marker_orange")
	);

	public static final AABB BLOCK_MARKER_LINE_AABB = new AABB(0, 0, 0, 16, 3, 3);
	public static final AABB CORNER_MARKER_AABB = new AABB(0, 0, 0, 16, 16, 16);
	public static final AABB CORNER_MARKER_ORANGE_AABB = new AABB(0, 0, 0, 16, 16, 16);
}
