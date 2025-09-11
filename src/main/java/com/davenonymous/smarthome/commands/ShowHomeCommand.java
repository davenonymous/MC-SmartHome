package com.davenonymous.smarthome.commands;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.particles.ModelParticleOptions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ShowHomeCommand implements Command<CommandSourceStack> {
	public static ShowHomeCommand instance = new ShowHomeCommand();

	private ShowHomeCommand() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("show").requires(CommandSourceStack::isPlayer)
			.then(
				Commands.argument("name", StringArgumentType.string()).executes(instance)
			);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		String homeName = StringArgumentType.getString(context, "name");

		WorldSavedHomes data = WorldSavedHomes.get(context.getSource().getLevel());
		if(data.getHome(player, homeName).isEmpty()) {
			context.getSource().sendFailure(Component.literal(String.format("Home with name %s does not exist", homeName)));
			return 0;
		}
		HomeCore home = data.getHome(player, homeName).get();
		var center = home.bounds().getCenter();

		context.getSource().getLevel().sendParticles(
			player,
			new ModelParticleOptions(ResourceLocation.withDefaultNamespace("block/diamond_block"), List.of(), Vec3.ZERO, 200),
			false, center.x, center.y, center.z, 1, 0, 0, 0, 0f);

		context.getSource().sendSuccess(() -> Component.literal(String.format("Showing home: %s", homeName)), false);
		return 0;
	}
}
