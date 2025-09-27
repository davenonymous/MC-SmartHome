package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensor;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.SensorSettings;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.DeviceDataPayload;
import com.davenonymous.smarthome.networking.VisualizationDataPayload;
import com.davenonymous.smarthome.setup.content.ModSensors;
import com.davenonymous.smarthome.setup.content.ModVisualizations;
import com.davenonymous.smarthome.watcher.VizQueryDatabaseTask;
import com.davenonymous.smarthome.watcher.WorldWatcherPool;
import com.davenonymous.smarthome.watcher.WorldWatcherUtil;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.duckdb.DuckDBConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public record RequestVisualizationDataPayload(UUID homeId, UUID zone, ConfiguredDevice device, ResourceLocation sensorId, ResourceLocation vizId, IVisualizationSettings settings)  implements CustomPacketPayload {
	public static final Type<RequestVisualizationDataPayload> TYPE = new Type<>(SmartHome.resource("request_viz_data"));

	public static final StreamCodec<RegistryFriendlyByteBuf, RequestVisualizationDataPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, RequestVisualizationDataPayload::homeId,
		UUIDUtil.STREAM_CODEC, RequestVisualizationDataPayload::zone,
		ConfiguredDevice.STREAM_CODEC, RequestVisualizationDataPayload::device,
		ResourceLocation.STREAM_CODEC, RequestVisualizationDataPayload::sensorId,
		ResourceLocation.STREAM_CODEC, RequestVisualizationDataPayload::vizId,
		IVisualizationSettings.STREAM_CODEC, RequestVisualizationDataPayload::settings,
		RequestVisualizationDataPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(RequestVisualizationDataPayload payload, IPayloadContext context) {
		ServerPlayer player = (ServerPlayer) context.player();
		ServerLevel level = (ServerLevel) player.level();

		var homes = WorldSavedHomes.get(level);
		var optHome = homes.getHome(payload.homeId());
		if(optHome.isEmpty()) {
			return;
		}

		var home = optHome.get();
		if(!home.owner().equals(player.getUUID())) {
			return;
		}

		var optZone = home.getZone(payload.zone());
		if(optZone.isEmpty()) {
			return;
		}

		var zone = optZone.get();
		var device = payload.device();

		IVisualization<?, ?> viz = ModVisualizations.getById(payload.vizId);
		ISensor<?, ?> sensor = ModSensors.getById(payload.sensorId);
		SensorSettings sensorSettings = device.sensors().get(payload.sensorId);

		Function<DuckDBConnection, IVisualizationData> dbFunction = sensor.getVisualizationData(zone, device, sensorSettings, viz, payload.settings);
		VizQueryDatabaseTask.execute(dbFunction).thenAccept((vizData) -> {
			SmartHome.LOGGER.info("Sending viz data to player='{}' home='{}' zone='{}' device='{}' sensor='{}' viz='{}'", player.getGameProfile().getName(), home.name(), zone.name(), device.id(), sensor.id(), payload.vizId());
			context.reply(new VisualizationDataPayload(zone.home().id(), zone.id(), device, payload.sensorId, vizData));
		});
	}
}
