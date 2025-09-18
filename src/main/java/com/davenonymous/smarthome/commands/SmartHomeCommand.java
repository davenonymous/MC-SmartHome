package com.davenonymous.smarthome.commands;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.commands.debug.WindowCommand;
import com.davenonymous.smarthome.commands.duckdb.ExecuteStatementCommand;
import com.davenonymous.smarthome.commands.duckdb.RunQueryCommand;
import com.davenonymous.smarthome.commands.home.DeleteHomeCommand;
import com.davenonymous.smarthome.commands.home.ListHomesCommand;
import com.davenonymous.smarthome.commands.home.ShowHomeCommand;
import com.davenonymous.smarthome.commands.zone.CreateZoneCommand;
import com.davenonymous.smarthome.commands.zone.DeleteZoneCommand;
import com.davenonymous.smarthome.commands.zone.ListZonesCommand;
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
				.then(ShowHomeCommand.registerCommand(dispatcher))
				.then(DeleteHomeCommand.registerCommand(dispatcher))
				.then(Commands.literal("zone").then(Commands.argument("home", StringArgumentType.string())
					.then(CreateZoneCommand.registerCommand(dispatcher).requires(CommandSourceStack::isPlayer))
					.then(ListZonesCommand.registerCommand(dispatcher).requires(CommandSourceStack::isPlayer))
					.then(DeleteZoneCommand.registerCommand(dispatcher).requires(CommandSourceStack::isPlayer))
				))
		).then(Commands.literal("duckdb").requires(commandSourceStack -> commandSourceStack.hasPermission(4))
			.then(RunQueryCommand.registerCommand(dispatcher))
			.then(ExecuteStatementCommand.registerCommand(dispatcher))
		).then(Commands.literal("debug").requires(commandSourceStack -> commandSourceStack.hasPermission(4))
			.then(WindowCommand.registerCommand(dispatcher)));
	}
}
