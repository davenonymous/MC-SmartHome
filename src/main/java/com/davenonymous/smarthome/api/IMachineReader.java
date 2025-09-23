package com.davenonymous.smarthome.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public interface IMachineReader extends ISensor {
	Block requiredBlock();

	boolean isValid(Level level, BlockPos pos, BlockState state);
}
