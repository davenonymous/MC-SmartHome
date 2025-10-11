package com.davenonymous.smarthome.commands.duckdb;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.watcher.ActionDatabaseTask;
import com.davenonymous.smarthome.watcher.WorldWatcherPool;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.sql.SQLException;

public class StartWebUICommand implements Command<CommandSourceStack> {
	public static StartWebUICommand instance = new StartWebUICommand();

	private StartWebUICommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("start").executes(instance)
			.then(Commands.argument("port", IntegerArgumentType.integer(1024, 65535)).executes(instance)
			.then(Commands.argument("open-browser", BoolArgumentType.bool()).executes(instance)));
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		if(SmartHome.uiRunning) {
			context.getSource().sendFailure(Component.literal("UI is already running"));
			return 0;
		}

		if(WorldWatcherPool.taskQueue == null) {
			context.getSource().sendFailure(Component.literal("No database connection"));
			return 0;
		}

		int port;
		try {
			port = IntegerArgumentType.getInteger(context, "port");
		} catch(Exception ex) {
			port = 4213;
		}

		boolean openBrowser;
		try {
			openBrowser = BoolArgumentType.getBool(context, "open-browser");
		} catch(Exception ex) {
			openBrowser = true;
		}

		int finalPort = port;
		boolean finalOpenBrowser = openBrowser;
		var task = new ActionDatabaseTask(connection -> {
			try {
				var stmt = connection.createStatement();
				stmt.execute("SET ui_local_port = "+finalPort+"; CALL " + (finalOpenBrowser ? "start_ui()" : "start_ui_server()"));
				stmt.close();
				context.getSource().sendSuccess(() -> Component.literal("UI started"), true);
				SmartHome.LOGGER.info("DuckDB UI started on port {} by {}", finalPort, context.getSource().getTextName());
				SmartHome.uiRunning = true;
			} catch (SQLException e) {
				context.getSource().sendFailure(Component.literal("SQL Error: " + e.getMessage()));
			}
		});
		task.enqueue(WorldWatcherPool.taskQueue);
		return 0;
	}
}
