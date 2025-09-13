package com.davenonymous.smarthome.commands;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.particles.ModelParticleOptions;
import com.davenonymous.smarthome.particles.util.ParticlePositionData;
import com.davenonymous.smarthome.particles.util.ParticleShapeHelper;
import com.davenonymous.smarthome.setup.content.ModParticleModels;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
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
		home.updateBounds(); // just to be sure

		var model = ModParticleModels.BLOCK_MARKER_LINE;
		var modelAABB = ModParticleModels.BLOCK_MARKER_LINE_AABB;
		var particleShaper = new ParticleShapeHelper(modelAABB);
		var particlePositions = particleShaper.shape(home.shape());

		for(ParticlePositionData positionData : particlePositions) {
			var pos = positionData.pos();
			var dir = positionData.direction();
			List<Axis> rotationAxes = new ArrayList<>();
			if(dir == Axis.YP) {
				rotationAxes.add(Axis.ZP);
			} else if(dir == Axis.YN) {
				rotationAxes.add(Axis.ZN);
			} else if(dir == Axis.ZP) {
				rotationAxes.add(Axis.YP);
			} else if(dir == Axis.ZN) {
				rotationAxes.add(Axis.YN);
			} else if(dir == Axis.XN) {
				rotationAxes.add(Axis.YN);
				rotationAxes.add(Axis.YN);
			}

			context.getSource().getLevel().sendParticles(
				player,
				new ModelParticleOptions(model.id(), model.getVariant(), rotationAxes, modelAABB.getMaxPosition(), 100, 1.0f),
				false, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0f
			);
		}
		context.getSource().sendSuccess(() -> Component.literal(String.format("Showing home: %s", homeName)), false);
		return 0;
	}
}
