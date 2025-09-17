package com.davenonymous.smarthome.commands.home;

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

public class DeleteHomeCommand implements Command<CommandSourceStack> {
	public static DeleteHomeCommand instance = new DeleteHomeCommand();

	private DeleteHomeCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("delete").requires(CommandSourceStack::isPlayer).requires(commandSourceStack -> commandSourceStack.hasPermission(4))
			.then(
				Commands.argument("name", StringArgumentType.string()).executes(instance)
			);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		String homeName = StringArgumentType.getString(context, "name");

		WorldSavedHomes data = WorldSavedHomes.get(context.getSource().getLevel());
		if(data.getHome(player.getUUID(), homeName).isEmpty()) {
			context.getSource().sendFailure(Component.literal(String.format("Home with name %s does not exist", homeName)));
			return 0;
		}
		data.removeHome(player.getUUID(), homeName);

		context.getSource().sendSuccess(() -> Component.literal(String.format("Deleted home: %s", homeName)), true);
		return 0;
	}
}
