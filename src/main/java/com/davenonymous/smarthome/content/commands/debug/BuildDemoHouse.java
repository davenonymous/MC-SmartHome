package com.davenonymous.smarthome.content.commands.debug;

import com.davenonymous.smarthome.util.WorldPlacerUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class BuildDemoHouse implements Command<CommandSourceStack> {
	public static BuildDemoHouse instance = new BuildDemoHouse();

	private BuildDemoHouse() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("demo-house").then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(instance));
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
		ServerLevel world = context.getSource().getLevel();

		WorldPlacerUtil.build9by9(pos, world, WorldPlacerUtil.defaultMaterialGetter);
		context.getSource().sendSuccess(() -> Component.literal("Placed demo house!"), true);

		return 0;
	}


}
