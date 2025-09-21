package com.davenonymous.smarthome.commands.zone;

import com.davenonymous.smarthome.commands.PermissionLevel;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

public class CreateZoneCommand implements Command<CommandSourceStack> {
	public static CreateZoneCommand instance = new CreateZoneCommand();

	private CreateZoneCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("create").requires(PermissionLevel.isModerator())
			.then(
				Commands.argument("name", StringArgumentType.string()).then(
					Commands.argument("cornerA", BlockPosArgument.blockPos())
				.then(Commands.argument("cornerB", BlockPosArgument.blockPos()).executes(instance)))
			);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		String homeName = StringArgumentType.getString(context, "home");
		String zoneName = StringArgumentType.getString(context, "name");
		BlockPos cornerA = BlockPosArgument.getBlockPos(context, "cornerA");
		BlockPos cornerB = BlockPosArgument.getBlockPos(context, "cornerB");

		var level = context.getSource().getServer().overworld();
		WorldSavedHomes worldSavedHomes = WorldSavedHomes.get(level);
		var home = worldSavedHomes.getPlayerHome(player, homeName);
		if(home.isEmpty()) {
			context.getSource().sendFailure(Component.literal(String.format("Home with name %s does not exist", homeName)));
			return 1;
		}

		if(home.get().getZone(zoneName).isPresent()) {
			context.getSource().sendFailure(Component.literal(String.format("Zone with name %s already exists in home %s", zoneName, homeName)));
			return 1;
		}

		if(cornerA.equals(cornerB)) {
			context.getSource().sendFailure(Component.literal("Zone corners cannot be the same"));
			return 1;
		}

		double cornerAX = cornerA.getX();
		double cornerAY = cornerA.getY();
		double cornerAZ = cornerA.getZ();
		double cornerBX = cornerB.getX();
		double cornerBY = cornerB.getY();
		double cornerBZ = cornerB.getZ();

		AABB zoneBounds = new AABB(
			Math.min(cornerAX, cornerBX), Math.min(cornerAY, cornerBY), Math.min(cornerAZ, cornerBZ),
			Math.max(cornerAX, cornerBX) + 1, Math.max(cornerAY, cornerBY) + 1, Math.max(cornerAZ, cornerBZ) + 1
		);
		HomeZone newZone = new HomeZone(zoneName, zoneBounds);
		home.get().addZone(newZone);
		worldSavedHomes.setDirty();

		context.getSource().sendSuccess(() -> Component.literal(String.format("Created zone %s in home %s", zoneName, homeName)), true);
		return 0;
	}
}
