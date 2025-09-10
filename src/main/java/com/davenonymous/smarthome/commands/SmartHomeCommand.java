package com.davenonymous.smarthome.commands;

import com.davenonymous.smarthome.SmartHome;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SmartHomeCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> register(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal(SmartHome.MODID).then(
			Commands.literal("home")
				.then(ListHomesCommand.registerCommand(dispatcher))
				.then(CreateHomeCommand.registerCommand(dispatcher))
				.then(Commands.literal("zone").then(Commands.argument("home", StringArgumentType.string())
					.then(CreateZoneCommand.registerCommand(dispatcher).requires(CommandSourceStack::isPlayer))
					.then(ListZonesCommand.registerCommand(dispatcher).requires(CommandSourceStack::isPlayer))
				))
		);
	}
}
