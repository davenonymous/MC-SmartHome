package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.VisualizationDataPayload;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.setup.dynamic.ModVisualizations;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import com.davenonymous.smarthome.watcher.VizQueryDatabaseTask;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.duckdb.DuckDBConnection;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.Function;

@Packet
public record RequestVisualizationDataPayload(UUID homeId, UUID zone, ConfiguredDevice device, ResourceLocation sensorId, ResourceLocation vizId, IVisualizationSettings settings)  implements LibPacketPayload {
	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, RequestVisualizationDataPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, RequestVisualizationDataPayload::homeId,
		UUIDUtil.STREAM_CODEC, RequestVisualizationDataPayload::zone,
		ConfiguredDevice.STREAM_CODEC, RequestVisualizationDataPayload::device,
		ResourceLocation.STREAM_CODEC, RequestVisualizationDataPayload::sensorId,
		ResourceLocation.STREAM_CODEC, RequestVisualizationDataPayload::vizId,
		IVisualizationSettings.STREAM_CODEC, RequestVisualizationDataPayload::settings,
		RequestVisualizationDataPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
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
		HomeSensor<?, ?> sensor = ModSensors.getById(payload.sensorId);
		SensorSettings sensorSettings = device.sensors().get(payload.sensorId);

		var dbHandler = ModSensors.DB_HANDLERS.get(sensor.id());
		Function<DuckDBConnection, LinkedHashMap<Pair<Instant, Long>, ?>> dbFunction = dbHandler.getValues(device.id(), 0, level.getGameTime());

		//Function<DuckDBConnection, IVisualizationData> dbFunction = sensor.getVisualizationData(zone, device, sensorSettings, viz, payload.settings);
		VizQueryDatabaseTask.execute(dbFunction).thenAccept((vizData) -> {
			if(vizData == null) {
				SmartHome.LOGGER.warn("Failed to get viz data for player='{}' home='{}' zone='{}' device='{}' sensor='{}' viz='{}'", player.getGameProfile().getName(), home.name(), zone.name(), device.id(), sensor.id(), payload.vizId());
				return;
			}

			//noinspection unchecked
			var replyPayload = new VisualizationDataPayload(zone.home().id(), zone.id(), device, payload.sensorId, payload.vizId, (LinkedHashMap<Pair<Instant, Long>, ISensorData>) vizData);
			context.reply(replyPayload);
		});
	}
}
