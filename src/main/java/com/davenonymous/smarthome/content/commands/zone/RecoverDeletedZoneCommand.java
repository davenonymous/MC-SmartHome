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

import java.util.UUID;

public class RecoverDeletedZoneCommand implements Command<CommandSourceStack> {
	public static RecoverDeletedZoneCommand instance = new RecoverDeletedZoneCommand();

	private RecoverDeletedZoneCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("recover")
			.requires(PermissionLevel.isAdmin())
			.then(
				Commands.argument("name|uuid", StringArgumentType.string())
					.executes(instance)
			);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		String homeName = StringArgumentType.getString(context, "home");
		String zoneName = StringArgumentType.getString(context, "name");
		UUID zoneUUID = null;
		try {
			zoneUUID = UUID.fromString(zoneName);
		} catch(IllegalArgumentException ex) {
		}

		WorldSavedHomes data = WorldSavedHomes.get(context.getSource().getLevel());
		var optHome = data.getPlayerHome(player, homeName);
		if(optHome.isEmpty()) {
			context.getSource().sendFailure(Component.literal(String.format("Home with name %s does not exist", homeName)));
			return 1;
		}

		var home = optHome.get();
		var optZone = zoneUUID == null ? home.getZone(zoneName) : home.getZone(zoneUUID);
		if(optZone.isEmpty()) {
			context.getSource().sendFailure(Component.literal(String.format("Zone with %s %s does not exist in home %s", zoneUUID == null ? "name" : "UUID", zoneName, homeName)));
			return 1;
		}

		var zone = optZone.get();
		if(!zone.isDeleted()) {
			context.getSource().sendFailure(Component.literal(String.format("Zone %s is not deleted in home %s", zone.name(), homeName)));
			return 1;
		}

		zone.setDeleted(false);
		data.setDirty();
		context.getSource().sendSuccess(() -> Component.literal(String.format("Recovered zone %s from home %s", zone.name(), homeName)), true);
		return 0;
	}
}
