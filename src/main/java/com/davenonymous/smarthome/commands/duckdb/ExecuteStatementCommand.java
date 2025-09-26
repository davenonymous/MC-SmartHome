package com.davenonymous.smarthome.commands.duckdb;

import com.davenonymous.smarthome.watcher.DatabaseTask;
import com.davenonymous.smarthome.watcher.WorldWatcherPool;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.sql.SQLException;

public class ExecuteStatementCommand implements Command<CommandSourceStack> {
	public static ExecuteStatementCommand instance = new ExecuteStatementCommand();

	private ExecuteStatementCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("exec")
			.then(Commands.argument("statement", StringArgumentType.greedyString()).executes(instance));
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		if(WorldWatcherPool.taskQueue == null) {
			context.getSource().sendFailure(Component.literal("No database connection"));
			return 0;
		}

		String statement = StringArgumentType.getString(context, "statement");

		if(statement.startsWith("select") && statement.startsWith("SELECT")) {
			context.getSource().sendFailure(Component.literal("Use /smarthome db query for SELECT statements"));
			return 0;
		}

		String text = "Executing query: " + statement;
		context.getSource().sendSuccess(() -> Component.literal(text), true);

		WorldWatcherPool.taskQueue.offer(new DatabaseTask(connection -> {
			try {
				var stmt = connection.createStatement();
				boolean success = stmt.execute(statement);
				if(success) {
					context.getSource().sendSuccess(() -> Component.literal("Statement executed successfully, result is a ResultSet"), true);
				} else {
					int updateCount = stmt.getUpdateCount();
					context.getSource().sendSuccess(() -> Component.literal("Statement executed successfully, " + updateCount + " rows affected"), true);
				}
				stmt.close();
			} catch (SQLException e) {
				context.getSource().sendFailure(Component.literal("SQL Error: " + e.getMessage()));
			}
		}));

		return 0;
	}
}
