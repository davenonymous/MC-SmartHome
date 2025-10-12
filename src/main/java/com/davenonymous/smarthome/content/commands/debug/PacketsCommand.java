package com.davenonymous.smarthome.content.commands.debug;

import com.davenonymous.smarthome.setup.dynamic.ModPackets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class PacketsCommand implements Command<CommandSourceStack> {
	public static PacketsCommand instance = new PacketsCommand();

	private PacketsCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("packets")
			.then(Commands.argument("log", BoolArgumentType.bool())
				.executes(instance)
			);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		var enable = BoolArgumentType.getBool(context, "log");
		ModPackets.DEBUG_PACKETS = enable;
		return 0;
	}
}
