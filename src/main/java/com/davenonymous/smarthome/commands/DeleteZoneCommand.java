package com.davenonymous.smarthome.commands;

import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DeleteZoneCommand implements Command<CommandSourceStack> {
	public static DeleteZoneCommand instance = new DeleteZoneCommand();

	private DeleteZoneCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("delete")
			.then(
				Commands.argument("name", StringArgumentType.string())
					.requires(commandSourceStack -> commandSourceStack.hasPermission(4))
					.executes(instance)
			);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		String homeName = StringArgumentType.getString(context, "home");
		String zoneName = StringArgumentType.getString(context, "name");

		WorldSavedHomes data = WorldSavedHomes.get(context.getSource().getLevel());
		var optHome = data.getHome(player, homeName);
		if(optHome.isEmpty()) {
			context.getSource().sendFailure(Component.literal(String.format("Home with name %s does not exist", homeName)));
			return 1;
		}

		var home = optHome.get();
		if(home.getZone(zoneName).isEmpty()) {
			context.getSource().sendFailure(Component.literal(String.format("Zone with name %s does not exist in home %s", zoneName, homeName)));
			return 1;
		}

		home.deleteZone(zoneName);
		data.setDirty();
		context.getSource().sendSuccess(() -> Component.literal(String.format("Deleted zone %s from home %s", zoneName, homeName)), true);
		return 0;
	}
}
