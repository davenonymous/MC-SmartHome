package com.davenonymous.smarthome.content.commands.debug;

import com.davenonymous.smarthome.watcher.db.DBHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class QueriesCommand implements Command<CommandSourceStack> {
	public static QueriesCommand instance = new QueriesCommand();

	private QueriesCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("queries")
			.then(Commands.argument("log", BoolArgumentType.bool())
				.executes(instance)
			);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		var enable = BoolArgumentType.getBool(context, "log");
		DBHandler.DEBUG_DB_QUERIES = enable;
		return 0;
	}
}
