package com.davenonymous.smarthome.setup.event;


import com.davenonymous.smarthome.SmartHome;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = SmartHome.MODID)
public class CapabilityRegister {

	@SubscribeEvent
	public static void onCapabilityRegister(RegisterCapabilitiesEvent event) {

//		event.registerBlock(
//			Capabilities.ItemHandler.BLOCK,
//			BonsaiPotBlockEntity::getCapability,
//			ModBlocks.BONSAI_POT.get(),
//			ModBlocks.BONSAI_POT_SMALL.get()
//		);
	}

}
