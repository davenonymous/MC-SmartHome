package com.davenonymous.smarthome.commands;

import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ListHomesCommand implements Command<CommandSourceStack> {
	public static ListHomesCommand instance = new ListHomesCommand();

	private ListHomesCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("list")
			.then(
				Commands.argument("player", EntityArgument.player()).executes(instance)
			)
			.executes(instance);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player;
		try {
			player = EntityArgument.getPlayer(context, "player");
		} catch (IllegalArgumentException ex) {
			player = context.getSource().getPlayerOrException();
		}

		WorldSavedHomes data = WorldSavedHomes.get(context.getSource().getLevel());
		var homes = data.getHomes(player);

		String playerName = player.getName().getString();
		context.getSource().sendSuccess(() -> Component.literal("Homes for " + playerName + ": " + homes.size()), false);
		for(var home : homes) {
			var text = String.format(" - %s: %s", home.name(), home.bounds());
			context.getSource().sendSuccess(() -> Component.literal(text), false);
		}
		return 0;
	}
}
