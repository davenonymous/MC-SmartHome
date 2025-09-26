package com.davenonymous.smarthome.api;

import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.machinezoo.noexception.throwing.ThrowingConsumer;
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
import java.util.function.Consumer;

public interface ISensor {
	ResourceLocation id();

	boolean isValid(Level level, BlockPos pos, BlockState state);

	default boolean isGeneric() {
		return false;
	}

	default boolean shouldVisitAllBlocksInZone() {
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


	ThrowingConsumer<DuckDBConnection> NOOP = (connection) -> {};

	default ThrowingConsumer<DuckDBConnection> visitHome(ServerLevel server, HomeCore home, ConfiguredDevice device) throws SQLException {
		return NOOP;
	}
	default ThrowingConsumer<DuckDBConnection> visitZone(ServerLevel server, HomeZone zone, ConfiguredDevice device) throws SQLException {
		return NOOP;
	}
	default ThrowingConsumer<DuckDBConnection> visitZoneEntity(ServerLevel server, HomeZone zone, ConfiguredDevice device, Entity entity) throws SQLException {
		return NOOP;
	}
	default ThrowingConsumer<DuckDBConnection> visitZoneBlock(ServerLevel server, HomeZone zone, ConfiguredDevice device, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {
		return NOOP;
	}

}
