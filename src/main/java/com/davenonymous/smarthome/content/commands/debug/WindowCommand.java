package com.davenonymous.smarthome.content.commands.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class WindowCommand implements Command<CommandSourceStack> {
	public static WindowCommand instance = new WindowCommand();

	private WindowCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("window")
			.then(Commands.argument("width", IntegerArgumentType.integer(640))
				.then(Commands.argument("height", IntegerArgumentType.integer(360))
					.executes(instance)
				)
			);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		var width = IntegerArgumentType.getInteger(context, "width");
		var height = IntegerArgumentType.getInteger(context, "height");

		var window = Minecraft.getInstance().getWindow();
		window.setWindowed(width, height);
		return 0;
	}
}
