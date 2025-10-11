package com.davenonymous.smarthome.commands.duckdb;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.watcher.ActionDatabaseTask;
import com.davenonymous.smarthome.watcher.WorldWatcherPool;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.sql.SQLException;

public class StopWebUICommand implements Command<CommandSourceStack> {
	public static StopWebUICommand instance = new StopWebUICommand();

	private StopWebUICommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("stop").executes(instance);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		if(WorldWatcherPool.taskQueue == null) {
			context.getSource().sendFailure(Component.literal("No database connection"));
			return 0;
		}

		var task = new ActionDatabaseTask(connection -> {
			try {
				var stmt = connection.createStatement();
				stmt.execute("CALL stop_ui_server()");
				stmt.close();
				context.getSource().sendSuccess(() -> Component.literal("UI stopped"), true);
				SmartHome.uiRunning = false;
			} catch (SQLException e) {
				context.getSource().sendFailure(Component.literal("SQL Error: " + e.getMessage()));
			}
		});
		task.enqueue(WorldWatcherPool.taskQueue);
		return 0;
	}
}
