package com.davenonymous.smarthome.api;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.duckdb.DuckDBConnection;

import java.sql.SQLException;

public interface ISensor {
	ResourceLocation id();

	boolean isValid(Level level, BlockPos pos, BlockState state);

	default boolean isGeneric() {
		return false;
	}

	default String nameTranslationKey() {
		var dotted = id().getPath().replaceAll("/", ".");
		return id().getNamespace() + "." + dotted + ".name";
	}

	default String descriptionTranslationKey() {
		var dotted = id().getPath().replaceAll("/", ".");
		return id().getNamespace() + "." + dotted + ".description";
	}

	default void createTables(DuckDBConnection connection) throws SQLException {}

	default void visitHome(DuckDBConnection connection, ServerLevel server, HomeCore home) throws SQLException {}
	default void visitZone(DuckDBConnection connection, ServerLevel server, HomeZone zone) throws SQLException {}
	default void visitZoneEntity(DuckDBConnection connection, ServerLevel server, HomeZone zone, Entity entity) throws SQLException {}
	default void visitZoneBlock(DuckDBConnection connection, ServerLevel server, HomeZone zone, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {}

}
