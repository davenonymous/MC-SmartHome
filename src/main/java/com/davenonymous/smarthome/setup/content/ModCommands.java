package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.commands.SmartHomeCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = SmartHome.MODID)
public class ModCommands {
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		SmartHome.LOGGER.info("Registering commands");
		event.getDispatcher().register(SmartHomeCommand.register(event.getDispatcher()));
		ModCommands.register(event.getDispatcher(), event.getBuildContext());
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext pContext) {
		dispatcher.register(
			SmartHomeCommand.register(dispatcher)
		);
	}
}
