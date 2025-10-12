package com.davenonymous.smarthome.content.commands.zone;

import com.davenonymous.smarthome.content.commands.PermissionLevel;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ListZonesCommand implements Command<CommandSourceStack> {
	public static ListZonesCommand instance = new ListZonesCommand();

	private ListZonesCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("list").requires(PermissionLevel.isModerator()).executes(instance);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		String homeName = StringArgumentType.getString(context, "home");

		WorldSavedHomes data = WorldSavedHomes.get(context.getSource().getLevel());
		var home = data.getPlayerHome(player, homeName);
		if(home.isEmpty()) {
			context.getSource().sendFailure(Component.literal(String.format("Home with name %s does not exist", homeName)));
			return 1;
		}

		var zones = home.get().zones();
		String playerName = player.getName().getString();
		context.getSource().sendSuccess(() -> Component.literal(String.format("Zones in home %s for %s: %d", homeName, playerName, zones.size())), false);
		for(var zone : zones) {
			var text = String.format(" - %s -> %s%s: %s", zone.id(), zone.name(), zone.isDeleted() ? " [DELETED]" : "", zone.bounds());
			context.getSource().sendSuccess(() -> Component.literal(text), false);
		}

		return 0;
	}
}
