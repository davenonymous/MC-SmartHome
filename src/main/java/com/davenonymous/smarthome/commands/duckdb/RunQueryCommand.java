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

public class RunQueryCommand implements Command<CommandSourceStack> {
	public static RunQueryCommand instance = new RunQueryCommand();

	private RunQueryCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("query")
			.then(Commands.argument("statement", StringArgumentType.greedyString()).executes(instance));
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		if(WorldWatcherPool.taskQueue == null) {
			context.getSource().sendFailure(Component.literal("No database connection"));
			return 0;
		}

		String statement = StringArgumentType.getString(context, "statement");

		if(!statement.startsWith("select") && !statement.startsWith("SELECT")) {
			context.getSource().sendFailure(Component.literal("Only SELECT queries are allowed"));
			return 0;
		}

		if(!statement.contains("limit") && !statement.contains("LIMIT")) {
			context.getSource().sendFailure(Component.literal("Queries without LIMIT are not allowed. Querying with default limit 10."));
			statement = statement.trim();
			if(statement.endsWith(";")) {
				statement = statement.substring(0, statement.length() - 1);
			}
			statement += " LIMIT 10;";
		}

		String text = "Executing query: " + statement;
		context.getSource().sendSuccess(() -> Component.literal(text), true);

		String finalStatement = statement;
		WorldWatcherPool.taskQueue.offer(new DatabaseTask(connection -> {
			try {
				var stmt = connection.createStatement();
				var result = stmt.executeQuery(finalStatement);
				int columnCount = result.getMetaData().getColumnCount();
				while(result.next()) {
					StringBuilder row = new StringBuilder();
					for(int i = 1; i <= columnCount; i++) {
						row.append(result.getString(i));
						if(i < columnCount) {
							row.append(", ");
						}
					}
					context.getSource().sendSuccess(() -> Component.literal(row.toString()), false);
				}
				result.close();
				stmt.close();
			} catch (SQLException e) {
				context.getSource().sendFailure(Component.literal("SQL Error: " + e.getMessage()));
			}
		}));

		return 0;
	}
}
