package com.davenonymous.smarthome.api;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.duckdb.DuckDBConnection;

import java.sql.SQLException;

public interface ISensor {
	ResourceLocation getId();

	default void createTables(DuckDBConnection connection) throws SQLException {}

	default void visitHome(DuckDBConnection connection, MinecraftServer server, HomeCore home) throws SQLException {}
	default void visitZone(DuckDBConnection connection, MinecraftServer server, HomeZone zone) throws SQLException {}
	default void visitHomeEntity(DuckDBConnection connection, MinecraftServer server, HomeCore home, Entity entity) throws SQLException {}
	default void visitZoneEntity(DuckDBConnection connection, MinecraftServer server, HomeZone zone, Entity entity) throws SQLException {}
	default void visitHomeBlock(DuckDBConnection connection, MinecraftServer server, HomeCore home, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {}
	default void visitZoneBlock(DuckDBConnection connection, MinecraftServer server, HomeZone zone, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {}

}
