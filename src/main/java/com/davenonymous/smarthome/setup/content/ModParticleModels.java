package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.phys.AABB;

public class ModParticleModels {
	public static final ModelResourceLocation BLOCK_MARKER_LINE = ModelResourceLocation.standalone(
		SmartHome.resource("block/block_marker_line")
	);
	public static final AABB BLOCK_MARKER_LINE_AABB = new AABB(0, 0, 0, 16, 3, 3);
}
