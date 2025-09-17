package com.davenonymous.smarthome.commands.home;

import com.davenonymous.smarthome.data.HomeCore;
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

public class CreateHomeCommand implements Command<CommandSourceStack> {
	public static CreateHomeCommand instance = new CreateHomeCommand();

	private CreateHomeCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("create").requires(CommandSourceStack::isPlayer)
			.then(
				Commands.argument("name", StringArgumentType.string()).executes(instance)
			);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		String homeName = StringArgumentType.getString(context, "name");

		HomeCore newHome = new HomeCore(homeName);
		newHome.setOwner(player.getUUID());

		WorldSavedHomes data = WorldSavedHomes.get(context.getSource().getLevel());
		if(data.getHome(player.getUUID(), homeName).isPresent()) {
			context.getSource().sendFailure(Component.literal(String.format("Home with name %s already exists", homeName)));
			return 0;
		}
		data.addHome(newHome);

		context.getSource().sendSuccess(() -> Component.literal(String.format("Created home: %s", homeName)), true);
		return 0;
	}
}
