package com.davenonymous.smarthome.setup.event;


import com.davenonymous.smarthome.SmartHome;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = SmartHome.MODID)
public class RegisterModelsHandler {
	public static final ModelResourceLocation BLOCK_MARKER_LINE = ModelResourceLocation.standalone(
		SmartHome.resource("block/block_marker_line")
	);

	@SubscribeEvent // on the mod event bus only on the physical client
	public static void registerAdditional(ModelEvent.RegisterAdditional event) {
		event.register(BLOCK_MARKER_LINE);
	}
}
